package video.downloader.free.now.videos.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import video.downloader.free.now.videos.AppDelegate
import video.downloader.free.now.videos.BuildConfig
import video.downloader.free.now.videos.R
import video.downloader.free.now.videos.fragments.DownloadFragment
import video.downloader.free.now.videos.fragments.GalleryFragment
import video.downloader.free.now.videos.utils.*
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    val TAG by lazy { MainActivity::class.java.name }
    val REQUEST_PERMISSION_CODE = 1001
    val REQUEST_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    private var tabLayout: TabLayout? = null
    var viewPager: androidx.viewpager.widget.ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
// Pja - 18EFBDBA2B9AF97B1F8506DBCD6F8218
// Dada - AE874F67312F8E300B2B0D03DA15F86D

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(getString(R.string.nav_home))
        mHandler = Handler()

        if (BuildConfig.DEBUG) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(Arrays.asList("F1D9C9FCAE67970C90A3EF61534291F6")).build()
            )
        }
        isNeedGrantPermission()

//        if (!isNeedGrantPermission()) {
        setUpNavigationView()

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);

        changeActionBarTitleFont()
        setlayout()
        loadHomeFragment()
//        }

        getRemoteConfigValue()
    }

    private fun getRemoteConfigValue() {
        (this.application as AppDelegate).mFirebaseRemoteConfig.fetch((this.application as AppDelegate).minimumFetchTime)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    (this.application as AppDelegate).mFirebaseRemoteConfig.activate()
                }
                Log.d(
                    TAG,
                    "stable_version after fetch -> " + (this.application as AppDelegate).mFirebaseRemoteConfig.getString(
                        Tags.stable_version
                    )
                )
                checkAppUpdateRemoteConfig()
            }
    }

    private fun changeActionBarTitleFont() {
        val s = SpannableString(getString(R.string.app_name))
        s.setSpan(
            TypefaceSpan(
                this,
                "montserrat_regular.ttf"
            ), 0, s.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        supportActionBar?.setTitle(s)
    }


    fun setlayout() {
        viewPager = findViewById(R.id.viewpager) as androidx.viewpager.widget.ViewPager
        setupViewPager(viewPager!!)

        tabLayout = findViewById(R.id.tabs) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager)
        setupTabIcons();
        viewPager?.currentItem = 1


    }

    fun setupTabIcons() {
//        tabLayout?.getTabAt(0)?.setIcon(R.drawable.ic_download_color_24dp)
//        tabLayout?.getTabAt(1)?.setIcon(R.drawable.ic_gallery_color_24dp)
    }

    private fun setupViewPager(viewPager: androidx.viewpager.widget.ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(GalleryFragment(), "History")
        adapter.addFragment(DownloadFragment(), "Downloads")
        viewPager.adapter = adapter
    }

    public fun isNeedGrantPermission(): Boolean {
        try {
            if (IOUtils.hasMarsallow()) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        REQUEST_PERMISSION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@MainActivity,
                            REQUEST_PERMISSION
                        )
                    ) {
                        val msg =
                            String.format(
                                getString(R.string.format_request_permision),
                                getString(R.string.app_name)
                            )

                        val localBuilder = AlertDialog.Builder(this@MainActivity)
                        localBuilder.setTitle("Permission Required!")
                        localBuilder
                            .setMessage(msg).setNeutralButton(
                                "Grant"
                            ) { paramAnonymousDialogInterface, paramAnonymousInt ->
                                ActivityCompat.requestPermissions(
                                    this@MainActivity,
                                    arrayOf(REQUEST_PERMISSION),
                                    REQUEST_PERMISSION_CODE
                                )
                            }
                            .setNegativeButton(
                                "Cancel"
                            ) { paramAnonymousDialogInterface, paramAnonymousInt ->
                                paramAnonymousDialogInterface.dismiss()
                                finish()
                            }
                        localBuilder.show()

                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(REQUEST_PERMISSION),
                            REQUEST_PERMISSION_CODE
                        )
                    }
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (requestCode == REQUEST_PERMISSION_CODE) {
                if (grantResults != null && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //
                    setUpNavigationView()

                    // load toolbar titles from string resources
                    activityTitles =
                        getResources().getStringArray(R.array.nav_item_activity_titles);
                    getSupportActionBar()?.setDisplayShowHomeEnabled(true);

                    changeActionBarTitleFont()
                    setlayout()
                    loadHomeFragment()
                } else {
                    iUtils.ShowToast(
                        this@MainActivity, getString(
                            R.string.info_permission_denied
                        )
                    )

                    finish()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            iUtils.ShowToast(
                this@MainActivity, getString(
                    R.string.info_permission_denied
                )
            )
            finish()
        }

    }

    internal inner class ViewPagerAdapter(manager: androidx.fragment.app.FragmentManager) :
        androidx.fragment.app.FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<androidx.fragment.app.Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): androidx.fragment.app.Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: androidx.fragment.app.Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            menu.getItem(0).getIcon().setTint(Color.parseColor("#ffffff"))
            menu.getItem(1).getIcon().setTint(Color.parseColor("#ffffff"))
            menu.getItem(2).getIcon().setTint(Color.parseColor("#ffffff"))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_rate -> {
                AppDelegate.logFirebaseEvent(
                    "action_rate",
                    null
                )
                Utility.rateApp(this)
                true
            }
            R.id.action_share -> {
                AppDelegate.logFirebaseEvent(
                    "action_share",
                    null
                )
                Utility.shareAppUrl(this)
                true
            }
            R.id.action_download -> {
                CURRENT_TAG = TAG_PHOTOS
                navItemIndex = 1
                loadHomeFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    // index to identify current nav menu item
    var navItemIndex = 0

    // tags used to attach the fragments
    private val TAG_HOME = "Home"
    private val TAG_PHOTOS = "Downloaded"
    private val TAG_NOTIFICATIONS = "Share App"
    private val TAG_SETTINGS = "Rate App"
    private val TAG_ABOUT_US = "About Us"
    var CURRENT_TAG = TAG_HOME

    // flag to load home fragment when user presses back key
    private val shouldLoadHomeFragOnBackPress = true
    private var mHandler: Handler? = null

    private fun setUpNavigationView() {
        nav_view.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navItemIndex = 0
                    CURRENT_TAG = TAG_HOME
                    selectNavMenu()
                }
                R.id.nav_photos -> {
                    navItemIndex = 1
                    CURRENT_TAG = TAG_PHOTOS
                    selectNavMenu()
                }
                R.id.nav_notifications -> {
//                    navItemIndex = 3
//                    CURRENT_TAG = TAG_NOTIFICATIONS
                    Utility.shareAppUrl(this)
                    drawer_layout.closeDrawers()
                    selectNavMenu()
                    return@OnNavigationItemSelectedListener false
                }
                R.id.nav_settings -> {
//                    navItemIndex = 4
//                    CURRENT_TAG = TAG_SETTINGS
                    Utility.rateApp(this)
                    drawer_layout.closeDrawers()
                    selectNavMenu()
                    return@OnNavigationItemSelectedListener false
                }
                R.id.nav_about_us -> {
                    // launch new intent instead of loading fragment
                    startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                    drawer_layout.closeDrawers()
                    selectNavMenu()
                    return@OnNavigationItemSelectedListener false
                }
                R.id.nav_privacy_policy -> {
                    // launch new intent instead of loading fragment
                    startActivity(Intent(this@MainActivity, HelpActivity::class.java))
                    drawer_layout.closeDrawers()
                    return@OnNavigationItemSelectedListener false
                }
                else -> navItemIndex = 0
            }

            //Checking if the item is in checked state or not, if not make it in checked state
//            if (menuItem.isChecked) {
//                menuItem.isChecked = false
//            } else {
//                menuItem.isChecked = true
//            }
            menuItem.isChecked = true
            loadHomeFragment()
            true
        })
        val actionBarDrawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.openDrawer,
            R.string.closeDrawer
        ) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }
        }

        drawer_layout.setDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    lateinit var activityTitles: Array<String>
    private fun setToolbarTitle() {
        supportActionBar?.setTitle(activityTitles.get(navItemIndex))
    }

    private fun selectNavMenu() {
        nav_view.getMenu().getItem(navItemIndex).isChecked = false
    }

    private fun loadHomeFragment() {
        selectNavMenu()
        setToolbarTitle()
        if (CURRENT_TAG == TAG_NOTIFICATIONS || CURRENT_TAG == TAG_SETTINGS ||
            supportFragmentManager.findFragmentByTag(CURRENT_TAG) != null
        ) {
            drawer_layout.closeDrawers()
            return
        }

        val mPendingRunnable = Runnable {
            val fragment: Fragment = getHomeFragment()!!
            val fragmentTransaction: FragmentTransaction =
                supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG)
            fragmentTransaction.commitAllowingStateLoss()
        }

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler?.post(mPendingRunnable)
        }
        //Closing drawer_layout on item click
        drawer_layout.closeDrawers()

        // refresh toolbar menu
        invalidateOptionsMenu()
        selectNavMenu()
    }

    private fun getHomeFragment(): Fragment? {
        return when (navItemIndex) {
            0 -> {
                // home
                DownloadFragment()
            }
            1 -> {
                // photos
                GalleryFragment()
            }
//            2 -> {
//                // movies fragment
//                MoviesFragment()
//            }
//            3 -> {
//                // notifications fragment
//                NotificationsFragment()
//            }
//            4 -> {
//                // settings fragment
//                SettingsFragment()
//            }
            else -> DownloadFragment()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawers()
            return
        }
        if (shouldLoadHomeFragOnBackPress) {
            if (navItemIndex !== 0) {
                navItemIndex = 0
                CURRENT_TAG = TAG_HOME
                loadHomeFragment()
                return
            }
        }
        super.onBackPressed()
    }

    /*Check for App update*/
    var showUpdate = false
    var updateStatus = 0

    fun checkAppUpdateRemoteConfig() {
        var stableVersion =
            (application as AppDelegate).mFirebaseRemoteConfig?.getString(
                Tags.stable_version
            )!!
        var currentVersion = packageManager.getPackageInfo(packageName, 0).versionName

        updateStatus = getUpdateStatus(currentVersion, stableVersion!!)
        if (updateStatus == UPDATE_COMPULSORY) {
            showAppUpdateAlert(
                this@MainActivity,
                getUpdateStatus(currentVersion, stableVersion!!/*"1.29.1"*/)
            )
            Utility.LogT("setDefaultConfig -> updateStatus -> $updateStatus")
            showUpdate = true
        } else if (updateStatus == UPDATE_OPTIONAL) {
            showAppUpdateAlert(this@MainActivity, updateStatus)
            showUpdate = true
        } else {
            checkInternetConnectivity()
        }
    }

    private fun checkInternetConnectivity() {

    }

    val MY_APP_UPDATE_REQUEST = 2020
    fun checkAppUpdate() {
        // Creates instance of the manager.
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    MY_APP_UPDATE_REQUEST
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_APP_UPDATE_REQUEST) {
            if (resultCode != RESULT_OK) {
                checkAppUpdate()
            }
        }
    }


    var currentVersion = "0"
    var newVersion = "0"
    var needToShowAlways = true
    val UPDATE_COMPULSORY = 1
    val UPDATE_OPTIONAL = 2
    val NO_UPDATE = 0
    var last_status = NO_UPDATE

    var alertDialog: androidx.appcompat.app.AlertDialog? = null

    fun showAppUpdateAlert(mActivity: Activity, last_status: Int): Int {
        if (last_status == NO_UPDATE)
            return last_status
        //show dialog
        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog!!.dismiss()
        }
        val alertdialog = androidx.appcompat.app.AlertDialog.Builder(mActivity)
        //                    alertdialog.setTitle("Please Update App");
        alertdialog.setMessage("A newer version of this App is available.")
        alertdialog.setPositiveButton("Update") { dialog, which ->
            //    Toast.makeText(this,"Update",Toast.LENGTH_LONG).show();
            val appPackageName =
                mActivity.packageName // getPackageName() from Context or Activity object
            try {
                mActivity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                Utility.LogE(anfe)
                mActivity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }

        when (last_status) {
            UPDATE_COMPULSORY -> {

                alertdialog.setCancelable(false)
                alertDialog = alertdialog.show()
            }
            UPDATE_OPTIONAL -> {
                alertdialog.setCancelable(true)
                alertdialog.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
                alertDialog = alertdialog.show()
            }
        }
        Utility.applyFontAtAlertDialog(mActivity.applicationContext, alertDialog)
        return last_status
    }

    fun getUpdateStatus(currentVersion: String, updatedVersion: String): Int {
        last_status = NO_UPDATE
        Utility.LogT("currentVersion => $currentVersion, updatedVersion => $updatedVersion")
        if (AppDelegate.isValidString(
                currentVersion
            )
        ) {
            var tokens = StringTokenizer(currentVersion, ".")
            val cv_first = tokens.nextToken()// this will contain "Fruit"
            val cv_second = tokens.nextToken()// this will contain " they taste good"

            tokens = StringTokenizer(updatedVersion, ".")
            val uv_first = tokens.nextToken()   // this will contain "Fruit"
            val uv_second = tokens.nextToken()  // this will contain " they taste good"

            val cv_array =
                currentVersion.split(".".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val uv_array =
                updatedVersion.split(".".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            Utility.LogT("cv_array => " + cv_array.size + ", uv_array => " + uv_array.size)

            Utility.LogT("getUpdagetUpdateStatusteStatus => $uv_first, $cv_first")
            Utility.LogT("getUpdateStatus => $uv_second, $cv_second")
            if (Integer.parseInt(uv_first) > Integer.parseInt(cv_first)) {
                last_status = UPDATE_COMPULSORY
                needToShowAlways = true
            } else if (Integer.parseInt(uv_first) == Integer.parseInt(cv_first)) {
                if (Integer.parseInt(uv_second) > Integer.parseInt(cv_second)) {
                    last_status = UPDATE_OPTIONAL
                    needToShowAlways = false
                } else {
                    last_status = NO_UPDATE
                    needToShowAlways = false
                }
            } else {
                last_status = NO_UPDATE
                needToShowAlways = false
            }
        }
        Utility.LogT("getUpdateStatus => last_status -> $last_status")
        return last_status
    }

    override fun onResume() {
        super.onResume()
        if (showUpdate) {
            if (updateStatus == UPDATE_COMPULSORY) {
                showAppUpdateAlert(this@MainActivity, updateStatus)
                Utility.LogT("setDefaultConfig -> updateStatus -> $updateStatus")
                showUpdate = true
            } else if (updateStatus == UPDATE_OPTIONAL) {
                showAppUpdateAlert(this@MainActivity, updateStatus)
                showUpdate = true
            } else {
//                CheckInternetConnectivity()
                showUpdate = false
            }
        }
    }
/*Handling force and optional update. */
}