package com.tsunderead.tsundoku.manga_reader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tsunderead.tsundoku.R
import com.tsunderead.tsundoku.api.MangaChapter
import com.tsunderead.tsundoku.api.NetworkCaller
import com.tsunderead.tsundoku.chapter.chapter_page.ChapterPage
import com.tsunderead.tsundoku.chapter.chapter_page.ChapterPageAdapter
import com.tsunderead.tsundoku.databinding.MangaReaderBinding
import com.tsunderead.tsundoku.manga_card_cell.Manga
import com.tsunderead.tsundoku.utils.OnSwipeTouchListener
import org.json.JSONObject
import kotlin.math.abs
import kotlin.properties.Delegates

class MangaReaderActivity : AppCompatActivity(), NetworkCaller<JSONObject> {
    private lateinit var mangaReaderBinding: MangaReaderBinding
    private lateinit var chapterList: ArrayList<String>
    private var position by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mangaReaderBinding = MangaReaderBinding.inflate(layoutInflater)
        setContentView(mangaReaderBinding.root)
        val chapterId = intent.getStringExtra("ChapterId")
        chapterList = intent.getStringArrayListExtra("chapterList")!!
        position = intent.getIntExtra("position", -1)
        Log.i("Stuff1", chapterId.toString())
        Log.i("Stuff2", chapterList.toString())
        Log.i("Stuff3", position.toString())
        chapterId?.let { MangaChapter(this, it) }?.execute()
        hideSystemBars()
        mangaReaderBinding.goBack.setOnClickListener {
            mangaReaderBinding.goBack.show()
            finish()
        }
        mangaReaderBinding.nextChapter.setOnClickListener {
            mangaReaderBinding.nextChapter.show()
            if (position == chapterList.size - 1) {
                Toast.makeText(this@MangaReaderActivity, "No Chapter After", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this@MangaReaderActivity, MangaReaderActivity::class.java)
                intent.putExtra("ChapterId", chapterList[position + 1])
                intent.putExtra("chapterList", chapterList)
                intent.putExtra("position", position + 1)
                startActivity(intent)
                finish()
            }
        }
        mangaReaderBinding.previousChapter.setOnClickListener {
            mangaReaderBinding.nextChapter.show()
            if (position == 0) {
                Toast.makeText(this@MangaReaderActivity, "No Chapter Before", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this@MangaReaderActivity, MangaReaderActivity::class.java)
                intent.putExtra("ChapterId", chapterList[position - 1])
                intent.putExtra("chapterList", chapterList)
                intent.putExtra("position", position - 1)
                startActivity(intent)
                finish()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onCallSuccess(result: JSONObject?) {
        Log.i("MangaReaderActivity", result.toString())
        val chapterPages = ArrayList<ChapterPage>()
        for (key in result!!.keys()) {
            val chapterPage = ChapterPage(key.toInt(), result.getString(key))
            chapterPages.add(chapterPage)
        }
        val gestureDetector = GestureDetector(this@MangaReaderActivity, object: GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                Log.i("Very Weird", "This Behaviour")
                if (mangaReaderBinding.nextChapter.isVisible) {
                    mangaReaderBinding.nextChapter.hide()
                    mangaReaderBinding.previousChapter.hide()
                    mangaReaderBinding.goBack.hide()
                } else {
                    mangaReaderBinding.nextChapter.show()
                    mangaReaderBinding.previousChapter.show()
                    mangaReaderBinding.goBack.show()
                }
                return true
            }
        })
        val recyclerView = findViewById<RecyclerView>(R.id.mangaReaderRecyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return gestureDetector.onTouchEvent(e)
            }
        })
        recyclerView.adapter = ChapterPageAdapter(chapterPages)
    }

}