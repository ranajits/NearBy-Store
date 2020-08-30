package com.ranajit.nearbystore.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.like.LikeButton
import com.like.OnLikeListener
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import com.ranajit.nearbystore.R
import com.ranajit.nearbystore.adapter.ImageAdapter
import com.ranajit.nearbystore.db.ProductDatabase
import com.ranajit.nearbystore.model.Favorite
import com.ranajit.nearbystore.model.ProductDetails
import com.ranajit.nearbystore.utilitis.Constants.ProductType.BOTTOM
import com.ranajit.nearbystore.utilitis.Constants.ProductType.TOP
import com.ranajit.nearbystore.utilitis.PermissionUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity() {
    private var isTopImage = false
    var topId = 0
    var bottomId = 0
    private var idCombination: String? = null
    private var newCombi: String? = null
    private val newCombiList: MutableList<String?>? = ArrayList()
    private var numberInString: List<String>? = null
    var topImageAdapter: ImageAdapter? = null
    var bottomImageAdapter: ImageAdapter? = null
    var animation: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        initViewPager()
        setDataFromDb(
            ProductDatabase.getInstance(this)!!
                .productDao()?.allOfflineData as List<ProductDetails>
        )
        pageChangeListener()
        likeButtonInit()
        animation =
            AnimationUtils.loadAnimation(this@HomeActivity, R.anim.rotate_animation)
        imgTopAdd.setOnClickListener {
            isTopImage = true
            if (isReadWritePermissionGranted) {
                selectImage(101)
            } else {
                PermissionUtil.requestPermissions(
                    this@HomeActivity,
                    REQUEST_READ_WRITE_PERMISSION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }

        imgBottomAdd.setOnClickListener {
            isTopImage = false
            if (isReadWritePermissionGranted) {
                selectImage(102)
            } else {
                PermissionUtil.requestPermissions(
                    this@HomeActivity,
                    REQUEST_READ_WRITE_PERMISSION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }

        imgShuffle.setOnClickListener {
            if (topImageAdapter?.count != null && topImageAdapter?.count!! > 0
                && bottomImageAdapter?.count != null && bottomImageAdapter?.count!! > 0
            ) {
                imgShuffle.startAnimation(animation)
                shuffle()
            } else {
                Toast.makeText(this, "Add some products first!", Toast.LENGTH_SHORT).show()
            }
        }

        txt_top.setOnClickListener {
            imgTopAdd.performClick()
        }
        txt_bottom.setOnClickListener {
            imgBottomAdd.performClick()
        }
    }

    private fun shuffle() {

        val offlineDataList: List<ProductDetails> = ProductDatabase.getInstance(this)!!
            .productDao()?.allOfflineData as List<ProductDetails>
        Collections.shuffle(offlineDataList)
        setDataFromDb(offlineDataList)

    }

    private fun selectImage(req: Int) {
        val items = arrayOf<CharSequence>(
            "Take Photo", "Choose from Library",
            "Cancel"
        )
        val builder = Builder(this@HomeActivity)
        builder.setItems(items) { dialog, item ->
            when {
                items[item] == "Take Photo" -> {
                    ImagePicker.with(this).setCameraOnly(true).setRequestCode(req).setMaxSize(1024)
                        .start()
                }
                items[item] == "Choose from Library" -> {
                    ImagePicker.with(this)
                        .setFolderMode(true)
                        .setFolderTitle("Select Shirts")
                        .setDirectoryName("Image Picker")
                        .setMultipleMode(true)
                        .setShowCamera(false)
                        .setShowNumberIndicator(true)
                        .setMaxSize(5)
                        .setToolbarColor("#ffffff")
                        .setToolbarIconColor("#585c8d")
                        .setToolbarTextColor("#585c8d")
                        .setBackgroundColor("#ffffff")
                        .setLimitMessage("You can select up to 5 Shirts")
                        .setRequestCode(req)
                        .start(); }
                items[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun setDataFromDb(offlineDataList: List<ProductDetails>) {

        val newOfflineTopDataList: ArrayList<ProductDetails> = ArrayList()
        val newOfflineBottomDataList: ArrayList<ProductDetails> = ArrayList()
        for (productDetails in offlineDataList) {
            if (productDetails.productType == TOP) {
                newOfflineTopDataList.add(productDetails)
            } else {
                newOfflineBottomDataList.add(productDetails)
            }
        }
        if (newOfflineTopDataList.size > 0) {
            topImageAdapter?.setProductList(newOfflineTopDataList)
            vpTop.adapter = topImageAdapter
            txt_top.visibility = View.GONE
        } else {
            txt_top.visibility = View.VISIBLE
        }

        if (newOfflineBottomDataList.size > 0) {
            bottomImageAdapter?.setProductList(newOfflineBottomDataList)
            txt_bottom.visibility = View.GONE
            vpBottom.adapter = bottomImageAdapter
        } else {
            txt_bottom.visibility = View.VISIBLE
        }

        if (newOfflineTopDataList.size > 0 && newOfflineBottomDataList.size > 0) {
            setShuffleEnable(true)
        } else {
            setShuffleEnable(false)
        }
    }

    private fun setTopDataFromDb() {
        val offlineDataList: List<ProductDetails> = ProductDatabase.getInstance(this)!!
            .productDao()?.allOfflineData as List<ProductDetails>
        val currentPos = topImageAdapter?.count
        topImageAdapter?.clearList()
        var newOfflineDataList: ArrayList<ProductDetails> = ArrayList()

        for (productDetails in offlineDataList) {
            if (productDetails.productType == TOP) {
                newOfflineDataList.add(productDetails)
            }
        }
        if (newOfflineDataList.size > 0) {
            topImageAdapter?.setProductList(newOfflineDataList)
            txt_top.visibility = View.GONE
        } else {
            txt_top.visibility = View.VISIBLE
            setShuffleEnable(false)
        }

        if (topImageAdapter?.count != null && topImageAdapter?.count!! > 0
            && bottomImageAdapter?.count != null && bottomImageAdapter?.count!! > 0
        ) {
            setShuffleEnable(true)
        }
        if (currentPos != null && topImageAdapter?.count != 0) {
            vpTop.setCurrentItem(currentPos, true)
        }
    }

    private fun setBottomDataFromDb() {
        val offlineDataList: List<ProductDetails> = ProductDatabase.getInstance(this)!!
            .productDao()?.allOfflineData as List<ProductDetails>
        val currentPos = bottomImageAdapter?.count
        bottomImageAdapter?.clearList()
        var newOfflineDataList: ArrayList<ProductDetails> = ArrayList()
        for (productDetails in offlineDataList) {
            if (productDetails.productType == BOTTOM) {
                newOfflineDataList.add(productDetails)
            }
        }
        if (newOfflineDataList.size > 0) {
            bottomImageAdapter?.setProductList(newOfflineDataList)
            txt_bottom.visibility = View.GONE
        } else {
            txt_bottom.visibility = View.VISIBLE
            setShuffleEnable(false)
        }
        if (topImageAdapter?.count != null && topImageAdapter?.count!! > 0
            && bottomImageAdapter?.count != null && bottomImageAdapter?.count!! > 0
        ) {
            setShuffleEnable(true)
        }

        if (currentPos != null && bottomImageAdapter?.count != 0) {
            vpBottom.setCurrentItem(currentPos, true)
        }
    }

    private fun setShuffleEnable(on: Boolean) {
        if (on) {
            imgShuffle.visibility = View.VISIBLE
            likeButton.visibility = View.VISIBLE
        } else {
            imgShuffle.visibility = View.GONE
            likeButton.visibility = View.GONE
        }
    }

    private fun likeButtonInit() {
        likeButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                idCombination = "$topId,$bottomId"
                val favorite = Favorite()
                favorite.combinations = idCombination
                ProductDatabase.getInstance(this@HomeActivity)!!.productDao()!!
                    .insertFavorite(favorite)
            }

            override fun unLiked(likeButton: LikeButton) {
                idCombination = "$topId,$bottomId"
                ProductDatabase.getInstance(this@HomeActivity)!!.productDao()!!
                    .deleteFavorite(idCombination)
            }
        })
    }

    private fun pageChangeListener() {

        vpTop.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                val productDetailsArrayList = topImageAdapter!!.dataList
                val productDetails = productDetailsArrayList[position]
                topId = productDetails.id
                checkForFavorite()
            }

            override fun onPageSelected(position: Int) {
                // Check if this is the page you want.
            }
        })

        vpBottom.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                val productDetailsArrayList = bottomImageAdapter?.dataList
                val productDetails = productDetailsArrayList?.get(position)
                if (productDetails != null) {
                    bottomId = productDetails.id
                }
                checkForFavorite()
            }

            override fun onPageSelected(position: Int) {
                // Check if this is the page you want.
            }
        })
    }

    private fun checkForFavorite() {
        val favoriteList: List<Favorite?>? = ProductDatabase.getInstance(this)!!
            .productDao()?.allFavoriteData
        var isLike = false
        if (favoriteList != null) {
            for (favorite in favoriteList) {
                if (favorite?.combinations == "$topId,$bottomId") {
                    isLike = true
                    break
                }
            }
        }
        likeButton.isLiked = isLike
    }

    private fun initViewPager() {
        topImageAdapter = ImageAdapter(this)
        bottomImageAdapter = ImageAdapter(this)
        vpTop.adapter = topImageAdapter
        vpBottom.adapter = bottomImageAdapter
    }


    private val isReadWritePermissionGranted: Boolean
        private get() = PermissionUtil.checkPermissions(
            this@HomeActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (isTopImage) {
                    imgTopAdd.performClick()
                } else
                    imgBottomAdd.performClick()

            }
        } else {
            val fragmentArrayList: List<Fragment> = supportFragmentManager.fragments
            for (fragment in fragmentArrayList) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val productDatabase: ProductDatabase? = ProductDatabase.getInstance(this)
        if (ImagePicker.shouldHandleResult(requestCode, resultCode, data, 101)) {

            val image = ImagePicker.getImages(data)
            for (img in image) {
                val productDetails = ProductDetails()
                productDetails.productUri = img.path
                productDetails.productType = TOP
                productDatabase?.productDao()?.insertOfflineData(productDetails)
            }

        } else if (ImagePicker.shouldHandleResult(requestCode, resultCode, data, 102)) {
            val image = ImagePicker.getImages(data)
            for (img in image) {
                val productDetails = ProductDetails()
                productDetails.productUri = img.path
                productDetails.productType = BOTTOM
                productDatabase?.productDao()?.insertOfflineData(productDetails)
            }
        }
        if (requestCode == 101) {
            setTopDataFromDb()
        } else if (requestCode == 102) {
            setBottomDataFromDb()
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_READ_WRITE_PERMISSION = 345
    }
}