package com.tsunderead.tsundoku.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.tsunderead.tsundoku.api.MangaWithCover
import com.tsunderead.tsundoku.api.NetworkCaller
import com.tsunderead.tsundoku.databinding.FragmentSearchBinding
import com.tsunderead.tsundoku.manga_card_cell.CardCellAdapter
import com.tsunderead.tsundoku.manga_card_cell.Manga
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Search : Fragment(), NetworkCaller<JSONObject> {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
//
    private lateinit var binding: FragmentSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSearchButton()
        initRecyclerView()

        val callApi = MangaWithCover(this)
        callApi.execute(0)
    }

    override fun onCallSuccess(result: JSONObject?) {
        Log.i("ok", result.toString())
        binding.includedFront.searchProgressIndicator.isIndeterminate = false
        val mangaList = ArrayList<Manga>()
        for (i in result!!.keys()) {
            val manga1 = Manga(
                result.getJSONObject(i).getString("cover_art"),
                result.getJSONObject(i).getString("author"),
                result.getJSONObject(i).getString("name"),
                result.getJSONObject(i).getString("id"))
            mangaList.add(manga1)
        }

        val adapter = CardCellAdapter(mangaList)
        binding.includedFront.exploreRecylcerView.adapter = adapter
    }

    override fun onCallFail() {
        Log.i("ok", "indeed")
    }

    private fun initRecyclerView () {
        val recyclerView = binding.includedFront.exploreRecylcerView

        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.setHasFixedSize(true)
    }
    private fun initSearchButton () {

        val mangaSearchBox = binding.includedBack.mangaSearchBox

        binding.includedBack.mangaSearchButton.setOnClickListener {
            binding.includedFront.searchProgressIndicator.isIndeterminate = true
            val filterMap = HashMap<String, Array<String>>()
            if (!mangaSearchBox.text.isNullOrEmpty()) filterMap["title"] = arrayOf(mangaSearchBox.text.toString())
            val checkedChipIds = binding.includedBack.chipGroupGenre.checkedChipIds
            val checkedChipList = Array(checkedChipIds.size){""}
            for (i in 0 until checkedChipIds.size)
                checkedChipList[i] = binding.includedBack.chipGroupGenre.findViewById<Chip>(checkedChipIds[i]).text as String
            filterMap["includedTags%5B%5D"] = checkedChipList // %5B%5D = []; this is how you pass arrays
            for (key in filterMap.keys) {
                val arr = filterMap[key]
                if (arr != null) {
                    for (a in arr) Log.i(key, a)
                }
            }

            binding.mangaSearchBackdrop.close()
            binding.includedFront.exploreRecylcerView.adapter = CardCellAdapter(ArrayList())
            MangaWithCover(this, filterMap).execute(0)
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Search().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}