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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonObjectRequest
import js.projects.newsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAdapter:NewsAdapter
    private lateinit var drawer:DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.title = getString(R.string.top_headlines)

        binding.navigationView.setCheckedItem(R.id.nav_top)

        binding.navigationView.setNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.nav_top -> {
                    fetchData("")
                    supportActionBar?.title = getString(R.string.top_headlines)
                    drawer.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.sports -> {
                    fetchData("&category=sports")
                    supportActionBar?.title = getString(R.string.sports)
                    drawer.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.business ->{
                    fetchData("&category=business")
                    supportActionBar?.title = getString(R.string.business)
                    drawer.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.entertainment ->{
                    fetchData("&category=entertainment")
                    supportActionBar?.title = getString(R.string.entertainment)
                    drawer.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.technology ->{
                    fetchData("&category=technology")
                    supportActionBar?.title = getString(R.string.technology)
                    drawer.closeDrawer(GravityCompat.START)
                    true
                }
                else ->{
                    true
                }
            }
        }

        drawer = binding.drawerLayout
        val drawerToggle = ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close)
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        fetchData("")
        mAdapter = NewsAdapter(this)
        binding.recyclerView.adapter = mAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.overflow_menu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawer.openDrawer(GravityCompat.START)
                true
            }
            R.id.learn_more ->{
                val builder = CustomTabsIntent.Builder()
                val url = ""
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(url))
                true
            }
            R.id.about_me ->{
                val builder = CustomTabsIntent.Builder()
                val url = ""
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(url))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    private fun fetchData(category: String){
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

        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonRequest)
    }
}