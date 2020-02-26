package org.shadowrunrussia2020.android.common.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class LinearSpaceItemDecoration(private val mSpace: Int) : ItemDecoration() {
    private var mDrawOnLast = false

    constructor(space: Int, drawOnLast: Boolean) : this(space) {
        mDrawOnLast = drawOnLast
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val layoutManager = parent.layoutManager
        if (!LinearLayoutManager::class.java.isInstance(layoutManager)) {
            return
        }
        val linearLayoutManager = layoutManager as LinearLayoutManager?
        if (!mDrawOnLast && parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
            return
        }
        if (linearLayoutManager!!.orientation == LinearLayoutManager.VERTICAL) {
            outRect.bottom = mSpace
        } else {
            outRect.right = mSpace
        }
    }

}