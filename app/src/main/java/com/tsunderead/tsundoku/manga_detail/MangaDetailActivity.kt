package com.tsunderead.tsundoku.manga_detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.tsunderead.tsundoku.R
import com.tsunderead.tsundoku.api.MangaChapterList
import com.tsunderead.tsundoku.api.NetworkCaller
import com.tsunderead.tsundoku.chapter.Chapter
import com.tsunderead.tsundoku.chapter.ChapterAdapter
import com.tsunderead.tsundoku.databinding.ActivityMangaDetailBinding
import com.tsunderead.tsundoku.manga_card_cell.Manga
import com.tsunderead.tsundoku.offlinedb.LibraryDBHelper
import org.json.JSONObject

class MangaDetailActivity : AppCompatActivity(), NetworkCaller<JSONObject>{
    private lateinit var libraryDBHandler : LibraryDBHelper
    private lateinit var binding: ActivityMangaDetailBinding
    private lateinit var manga: Manga
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMangaDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val cover = intent.getStringExtra("Cover")
        val author = intent.getStringExtra("Author")
        val title = intent.getStringExtra("Title")
        println("Cover is $cover")
        println("Title is $title")
        println("Author is $author")
        val authorId = findViewById<TextView>(R.id.author)
        val coverId = binding.mangacover
        authorId.text = author
        binding.mangaDetailCollapsebar.title = title
        val mangaId = intent.getStringExtra("MangaID")
        if (mangaId != null) {
            Log.d("mangaID", mangaId)
        }
        libraryDBHandler = LibraryDBHelper(this, null)
        manga = Manga(cover!!, author!!, title!!, mangaId!!)
        if (libraryDBHandler.isPresent(manga)) {
            findViewById<ImageButton>(R.id.addToLibrary).setImageResource(R.drawable.ic_baseline_favorite_24)
        }
        findViewById<ImageButton>(R.id.addToLibrary).setOnClickListener {
            if (libraryDBHandler.isPresent(manga)) {
                libraryDBHandler.deleteManga(manga.mangaId)
                findViewById<ImageButton>(R.id.addToLibrary).setImageResource(R.drawable.ic_baseline_favorite_border_24)
            } else {
                libraryDBHandler.insertManga(manga)
                findViewById<ImageButton>(R.id.addToLibrary).setImageResource(R.drawable.ic_baseline_favorite_24)
            }
        }
        Glide.with(this@MangaDetailActivity).load(cover).placeholder(R.drawable.placeholder).into(coverId)
        MangaChapterList(this, mangaId).execute(0)
        val chip = Chip(this)
        chip.text = "hello"
        binding.mangaIdChipgroup.addView(chip)
        val chip1 = Chip(this)
        chip1.text = "noki"
        binding.mangaIdChipgroup.addView(chip1)
    }

    override fun onCallSuccess(result: JSONObject?) {
        Log.i("MangaDetailActivity", result.toString())
        val recyclerView = findViewById<RecyclerView>(R.id.chapterRecyclerView)
        val layoutManager = LinearLayoutManager(this@MangaDetailActivity)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        val chapters = ArrayList<Chapter>()
        for (key in result!!.keys()) {
            val chapter = Chapter(result.getJSONObject(key).getInt("chapterNo"), result.getJSONObject(key).getString("chapterId"))
            chapters.add(chapter)
        }
        recyclerView.adapter = ChapterAdapter(manga, chapters)
    }
}