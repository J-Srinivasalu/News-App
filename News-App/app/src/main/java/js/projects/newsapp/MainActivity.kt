package js.projects.newsapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonObjectRequest
import js.projects.newsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAdapter:NewsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.title = getString(R.string.top_headlines)

        binding.navigationView.setCheckedItem(R.id.nav_top)

        var category = ""

        binding.navigationView.setNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.nav_top -> {
                    category = ""
                    fetchData(category)
                    supportActionBar?.title = getString(R.string.top_headlines)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.sports -> {
                    category = "&category=sports"
                    fetchData(category)
                    supportActionBar?.title = getString(R.string.sports)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.business ->{
                    category = "&category=business"
                    fetchData(category)
                    supportActionBar?.title = getString(R.string.business)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.entertainment ->{
                    category = "&category=entertainment"
                    fetchData(category)
                    supportActionBar?.title = getString(R.string.entertainment)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.technology ->{
                    category = "&category=technology"
                    fetchData(category)
                    supportActionBar?.title = getString(R.string.technology)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else ->{
                    true
                }
            }
        }

        val drawerToggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = NewsAdapter(this)
        binding.recyclerView.adapter = mAdapter
        fetchData(category)

        binding.swipeRefresh.setOnRefreshListener {
            fetchData(category)
        }
        binding.swipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow_menu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            R.id.learn_more ->{
                val builder = CustomTabsIntent.Builder()
                val url = "https://github.com/J-Srinivasalu/News-App"
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(url))
                true
            }
            R.id.about_me ->{
                val builder = CustomTabsIntent.Builder()
                val url = "https://github.com/J-Srinivasalu"
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(url))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    private fun fetchData(category: String){
        mAdapter.clear()
        val url = "https://newsapi.org/v2/top-headlines?country=in$category&apiKey=2cace4ab017b477abccca3a813ee67bb"
        val jsonRequest = object: JsonObjectRequest(
            Method.GET, url, null,{
                val newsJsonArray = it.getJSONArray("articles")
                val newsArray = ArrayList<News>()
                for(i in 0 until newsJsonArray.length()){
                    val newsJsonObject = newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("author"),
                        newsJsonObject.getString("publishedAt"),
                        newsJsonObject.getString("url"),
                        newsJsonObject.getString("urlToImage")
                    )
                    newsArray.add(news)
                }
                mAdapter.updateNews(newsArray)
            },
            {
                Toast.makeText(this,it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }
        binding.swipeRefresh.isRefreshing = false
        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonRequest)
    }
}