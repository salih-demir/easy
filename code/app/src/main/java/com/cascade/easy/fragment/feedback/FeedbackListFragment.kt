package com.cascade.easy.fragment.feedback

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.cascade.easy.R
import com.cascade.easy.adapter.FeedbackAdapter
import com.cascade.easy.adapter.FeedbackListener
import com.cascade.easy.adapter.itemdecoration.GridSpacingItemDecoration
import com.cascade.easy.data.AdapterType
import com.cascade.easy.data.SortType
import com.cascade.easy.fragment.base.BaseFragment
import com.cascade.easy.model.network.response.feedback.Feedback
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_feedback_list.*
import java.util.*

class FeedbackListFragment : BaseFragment(), FeedbackListener, Observer<Any> {
    private val liveFeedbackList = MutableLiveData<List<Feedback>>()
    private val liveAdapterType = MutableLiveData<AdapterType>()
    private val liveSortType = MutableLiveData<SortType>()

    private lateinit var feedbackAdapter: FeedbackAdapter
    private var feedbackList: List<Feedback>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_feedback_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpeechService()
        setupSwipeRefreshLayout()
        setupRecyclerView()
        setupSearchEditText()
        setupSortBy()
        setupAdapterTypes()

        fetchFeedbackData()
    }

    override fun onResume() {
        super.onResume()
        feedbackAdapter.listener = this
    }

    override fun onChanged(t: Any) {
        if (t is AdapterType) {
            adapterTypeChanged(t)
        } else if (t is SortType) {
            sortTypeChanged(t)
        }
    }

    override fun onCommentClicked(feedback: Feedback) {
        // Speak comment
        feedback.comment?.takeIf { it.isNotEmpty() }?.let { comment ->
            val locale = feedback.browser?.language?.let {
                val languageTag = it.take(2)
                Locale.forLanguageTag(languageTag)
            } ?: Locale.ENGLISH
            speakCommentWithLocale(comment, locale)
        }
    }

    override fun onLocationRequested(feedback: Feedback) {
        val geolocation = feedback.geolocation!!
        val lat = geolocation.lat.toString()
        val lon = geolocation.lon.toString()
        openMapsLocation(lat, lon)
    }

    override fun onInfoRequested(feedback: Feedback) {
        AlertDialog.Builder(context!!)
            .setTitle(R.string.app_name)
            .setMessage(feedback.browser?.appVersion)
            .setPositiveButton(R.string.label_ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener {
            fetchFeedbackData()
        }
    }

    private fun setupRecyclerView() {
        feedbackAdapter = FeedbackAdapter(liveFeedbackList)
        liveAdapterType.observe(this, this)
        liveAdapterType.postValue(DEFAULT_ADAPTER_TYPE)

        with(recyclerViewFeedbackList) {
            adapter = feedbackAdapter

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(DRAG_DIRECTIONS, 0) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition

                    val list = liveFeedbackList.value!!
                    Collections.swap(list, fromPos, toPos)
                    liveFeedbackList.value = list

                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            }).attachToRecyclerView(this)
        }
    }

    private fun setupSearchEditText() {
        editTextSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchComment(v.text)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        editTextSearch.addTextChangedListener {
            searchComment(it.toString())
        }
    }

    private fun setupSortBy() {
        liveSortType.observe(this, this)

        imageButtonSortBy.setOnClickListener {
            val selectedIndex = liveSortType.value?.ordinal ?: SortType.values()
                .indexOf(DEFAULT_SORT_TYPE)
            val sortTypes = SortType.values().map {
                getString(it.labelResId)
            }.toTypedArray()

            AlertDialog.Builder(context!!)
                .setTitle(R.string.title_sort_by)
                .setSingleChoiceItems(
                    sortTypes,
                    selectedIndex
                ) { dialog, which ->
                    dialog.dismiss()
                    val selectedSortType = SortType.values()[which]
                    liveSortType.postValue(selectedSortType)
                }.show()
        }
    }

    private fun setupAdapterTypes() {
        AdapterType.values().forEach {
            AppCompatRadioButton(radioGroupAdapterTypes.context).apply {
                id = View.generateViewId()
                tag = it
                isChecked = it == DEFAULT_ADAPTER_TYPE
                setCompoundDrawablesWithIntrinsicBounds(it.imageResId, 0, 0, 0)
            }.let {
                radioGroupAdapterTypes.addView(it)
            }
        }
        radioGroupAdapterTypes.setOnCheckedChangeListener { group, checkedId ->
            val radioButtonAdapterType = group.findViewById<RadioButton>(checkedId)
            val adapterType = radioButtonAdapterType.tag as AdapterType

            liveAdapterType.postValue(adapterType)
        }
    }

    private fun setupSpeechService() {
        // Reset locale of speech services after every utterance is completed.
        speechService.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {
                speechService.language = Locale.getDefault()

            }

            override fun onError(utteranceId: String?) {
                speechService.language = Locale.getDefault()
            }

            override fun onStart(utteranceId: String?) {}
        })
    }

    private fun fetchFeedbackData() {
        swipeRefreshLayout.isRefreshing = true

        networkService.feedbackList().send({
            if (activity?.isFinishing != false)
                return@send

            swipeRefreshLayout.isRefreshing = false
            feedbackList = it.items
            updateFeedbackList()
        }, {
            if (activity?.isFinishing != false)
                return@send

            swipeRefreshLayout.isRefreshing = false

            Snackbar.make(recyclerViewFeedbackList, it.message.toString(), Snackbar.LENGTH_LONG)
                .setAction(R.string.label_try_again) { fetchFeedbackData() }
                .show()
        })
    }

    private fun speakCommentWithLocale(comment: String, locale: Locale) {
        speechService.language = locale
        createMessage(comment)
    }

    private fun updateFeedbackList(
        sortType: SortType = liveSortType.value ?: DEFAULT_SORT_TYPE,
        searchText: CharSequence? = null
    ) {
        swipeRefreshLayout.isRefreshing = true

        feedbackList?.let { list ->
            when (sortType) {
                SortType.COMMENT_LENGTH -> list.sortedByDescending { it.comment?.length ?: 0 }
                SortType.RATING -> list.sortedByDescending { it.rating }
                else -> list
            }.filter {
                it.comment?.contains(searchText ?: "") ?: false
            }
        }?.let {
            swipeRefreshLayout.isRefreshing = false
            liveFeedbackList.postValue(it)
        }
    }

    private fun adapterTypeChanged(adapterType: AdapterType) {
        val spanCount = requireContext().resources.getInteger(R.integer.feedback_item_span_count)
        val spacing = context?.resources?.getDimensionPixelSize(R.dimen.content_spacing) ?: 0
        with(recyclerViewFeedbackList) {
            layoutManager = when (adapterType) {
                AdapterType.LIST -> LinearLayoutManager(context)
                AdapterType.GRID -> StaggeredGridLayoutManager(
                    spanCount,
                    StaggeredGridLayoutManager.VERTICAL
                )
            }

            for (i in 0 until itemDecorationCount) {
                getItemDecorationAt(i).takeIf { it is GridSpacingItemDecoration }
                    ?.let {
                        removeItemDecoration(it)
                    }
            }

            when (adapterType) {
                AdapterType.LIST -> GridSpacingItemDecoration(1, spacing)
                AdapterType.GRID -> GridSpacingItemDecoration(spanCount, spacing)
            }.let {
                addItemDecoration(it)
            }
        }
    }

    private fun sortTypeChanged(sortType: SortType) {
        updateFeedbackList(sortType = sortType)
    }

    private fun searchComment(searchText: CharSequence) {
        if (recyclerViewFeedbackList.scrollY > 0) {
            recyclerViewFeedbackList.smoothScrollToPosition(0)
        }
        updateFeedbackList(searchText = searchText)
    }

    private fun openGooglePlayMaps() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                "https://play.google.com/store/apps/details?id$GOOGLE_MAPS_PACKAGE_ID"
            )
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }

    private fun openMapsLocation(lat: String, lon: String) {
        val gmmIntentUri: Uri = Uri.parse("geo:$lat,$lon")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage(GOOGLE_MAPS_PACKAGE_ID)
        if (mapIntent.resolveActivity(context!!.packageManager) != null) {
            startActivity(mapIntent)
        } else {
            Snackbar.make(
                recyclerViewFeedbackList,
                R.string.message_google_maps_not_found,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.label_setup) { openGooglePlayMaps() }
                .show()
        }
    }

    companion object {
        private const val GOOGLE_MAPS_PACKAGE_ID = "com.google.android.apps.maps"
        private const val DRAG_DIRECTIONS = ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        private val DEFAULT_ADAPTER_TYPE = AdapterType.LIST
        private val DEFAULT_SORT_TYPE = SortType.NONE

        fun newInstance() = FeedbackListFragment()
    }
}