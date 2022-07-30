package com.sriyank.javatokotlindemo.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.design.widget.NavigationView
import com.sriyank.javatokotlindemo.adapters.DisplayAdapter
import com.sriyank.javatokotlindemo.models.Repository
import com.sriyank.javatokotlindemo.retrofit.GithubAPIService
import android.os.Bundle
import com.sriyank.javatokotlindemo.R
import android.support.v7.widget.LinearLayoutManager
import com.sriyank.javatokotlindemo.retrofit.RetrofitClient
import android.util.Log
import java.util.HashMap
import com.sriyank.javatokotlindemo.models.SearchResponse
import android.view.MenuItem
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import com.sriyank.javatokotlindemo.app.Constants
import com.sriyank.javatokotlindemo.app.Util
import kotlinx.android.synthetic.main.activity_display.*
import kotlinx.android.synthetic.main.header.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DisplayActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var displayAdapter: DisplayAdapter
    private var browsedRepositories: List<Repository> = mutableListOf()
    private val githubAPIService: GithubAPIService by lazy {
        RetrofitClient.githubAPIService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)


        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Showing Browsed Results")

        setAppUsername()


        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager


        navigationView.setNavigationItemSelectedListener(this)

        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        val intent = intent
        if (intent.getIntExtra(Constants.KEY_QUERY_TYPE, -1) == Constants.SEARCH_BY_REPO) {
            val queryRepo = intent.getStringExtra(Constants.KEY_REPO_SEARCH)
            val repoLanguage = intent.getStringExtra(Constants.KEY_LANGUAGE)
            fetchRepositories(queryRepo.toString(), repoLanguage.toString())
        } else {
            val githubUser = intent.getStringExtra(Constants.KEY_GITHUB_USER)
            fetchUserRepositories(githubUser)
        }
    }

    private fun setAppUsername() { //retrive data from a share preference

        val sp = getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val personName = sp.getString(Constants.KEY_PERSON_NAME, "User")

        val headerView = navigationView.getHeaderView(0)
        headerView.txvName.text = personName

    }

    private fun fetchUserRepositories(githubUser: String?) {

        githubAPIService.searchRepositoriesByUser(githubUser).enqueue(object : Callback<List<Repository>> {

            override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {

                if (response.isSuccessful) {
                    Log.i(TAG, "posts Loaded From API " + response)

                    //setting up thr recyler view
                    response.body()?.let{
                        browsedRepositories = it
                    }

                    if (browsedRepositories.isNotEmpty()) {
                        setupRecyclerView(browsedRepositories)
                    } else {
                        Util.showMessage(this@DisplayActivity, "No Items Found!!")
                    }

                } else {
                    Log.i(TAG, "Error" + response)
                    Util.showErrorMessage(this@DisplayActivity, response.errorBody())
                }
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Util.showMessage(this@DisplayActivity, t.message)
            }
        })

    }

    private fun fetchRepositories(queryRepo: String, repoLanguage: String) {
        var queryRepo = queryRepo
        val query: MutableMap<String, String?> = HashMap()
        if (repoLanguage.isNotEmpty())
            queryRepo += " language:" +repoLanguage
            query["q"] = queryRepo
        githubAPIService.searchRepositories(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    Log.i(TAG, "posts loaded from API $response")

                    //Safe Call with Let Operator
                    response.body()?.items?.let {
                        browsedRepositories = it
                    }
                    //End Safe Call with Let Operator

                    if (browsedRepositories.isNotEmpty())
                            setupRecyclerView(browsedRepositories)
                    else
                        Util.showMessage(this@DisplayActivity, "No Items Found")
                } else {
                    Log.i(TAG, "error $response")
                    Util.showErrorMessage(this@DisplayActivity, response.errorBody())
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Util.showMessage(this@DisplayActivity, t.toString())
            }
        })
    }

    private fun setupRecyclerView(items: List<Repository>) {
        displayAdapter = DisplayAdapter(this, items)
        recyclerView.adapter = displayAdapter
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        menuItem.isChecked = true
        closeDrawer()
        when (menuItem.itemId) {
            R.id.item_bookmark -> {
                showBookmarks()
                supportActionBar!!.title = "Showing Bookmarks"
            }
            R.id.item_browsed_results -> {
                showBrowsedResults()
                supportActionBar!!.title = "Showing Browsed Results"
            }
        }
        return true
    }

    private fun showBrowsedResults() {
        displayAdapter.swap(browsedRepositories)
    }

    private fun showBookmarks() {

    }

    private fun closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) closeDrawer() else {
            super.onBackPressed()

        }
    }

    companion object {
        private val TAG = DisplayActivity::class.java.simpleName
    }
}