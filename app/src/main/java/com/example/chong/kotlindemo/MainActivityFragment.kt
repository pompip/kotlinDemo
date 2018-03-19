package com.example.chong.kotlindemo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Toast
import com.example.chong.kotlindemo.entity.ChatMsg
import com.example.chong.kotlindemo.util.getChatMsg
import kotlinx.android.synthetic.main.item_view.view.*

/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    fun Fragment.toast(toast: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this.context, toast, length).show();
    }

    private lateinit var recycleView: RecyclerView;
    var currentPage: Int = 0;
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recycleView = view?.findViewById<RecyclerView>(R.id.recycle_view)!!;
        val list = arrayListOf<ChatMsg>()
        for (i in 0..9) {
            list.add(i, getChatMsg(i));
        }
        recycleView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            adapter = MyAdapter(list);
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            itemAnimator = DefaultItemAnimator()
            addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                val detector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                    override fun onSingleTapUp(e: MotionEvent?): Boolean {
                        val view = recycleView.findChildViewUnder(e?.x!!, e.y)
                        val childAdapterPosition = recycleView.getChildAdapterPosition(view);
                        this@MainActivityFragment.toast(list.get(childAdapterPosition).toString())
                        return true
                    }
                })

                override fun onInterceptTouchEvent(rv: RecyclerView?, e: MotionEvent?): Boolean {
                    return detector.onTouchEvent(e);
                }

            })
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView?.canScrollVertically(1)!!) {
                        currentPage++;
                        for (i in 0..9) {
                            val pos = i + currentPage * 10;
                            list.add(getChatMsg(pos))
                            recyclerView.adapter.notifyItemInserted(pos)
                        }
                    }
                }
            })
        }


    }

    class MyAdapter constructor(val list: ArrayList<ChatMsg>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            holder?.itemView?.text_title?.text = list.get(position).title;
            holder?.itemView?.text_content?.text = list.get(position).content;
        }

        override fun getItemCount(): Int {
            return list.size;
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_view, parent, false)) {}
        }

    }

}

