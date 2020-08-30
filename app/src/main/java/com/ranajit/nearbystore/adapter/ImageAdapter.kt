package com.ranajit.nearbystore.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ranajit.nearbystore.R
import com.ranajit.nearbystore.model.ProductDetails
import kotlinx.android.synthetic.main.item_image_layout.view.*

class ImageAdapter(private val context: Context) : PagerAdapter() {
    val dataList = ArrayList<ProductDetails>()

    override fun getCount(): Int {
        return dataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = layoutInflater.inflate(R.layout.item_image_layout, container, false)
        val productDetails = dataList[position]
        Glide.with(context)
            .load(productDetails.productUri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(itemView.topBottomImage)
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

    fun setProductList(dataLists: ArrayList<ProductDetails>) {
        dataList.clear()
        dataList.addAll(dataLists)
        notifyDataSetChanged()
    }

    fun clearList() {
        dataList.clear()
        notifyDataSetChanged()
    }

}