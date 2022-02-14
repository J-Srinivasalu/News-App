package js.projects.newsapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(private val context: Context): RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var items: ArrayList<News> = ArrayList()

    class NewsViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val author: TextView = itemView.findViewById(R.id.author)
        val date: TextView = itemView.findViewById(R.id.date)
        val image: ImageView = itemView.findViewById(R.id.image)
        val shareImage: ImageView = itemView.findViewById(R.id.share_image)
        val cardView: CardView = itemView.findViewById(R.id.card_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_card, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = items[position]
        holder.title.text = currentItem.title
        if(currentItem.author.lowercase() == "null"){
            holder.author.visibility = View.GONE
        }
        else{
            holder.author.visibility = View.VISIBLE
            holder.author.text = currentItem.author
        }

        holder.date.text = timeDifference(currentItem.date)
        Glide.with(holder.itemView.context).load(currentItem.imageUrl).into(holder.image)
        holder.shareImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT,currentItem.title+"\n"+currentItem.url)
            val chooser = Intent.createChooser(intent,"Share this news article with:")
            context.startActivity(chooser)
        }
        holder.cardView.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(currentItem.url))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // Clean all elements of the recycler
    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    // Add a list of items
    @SuppressLint("NotifyDataSetChanged")
    fun updateNews(updatedItems: ArrayList<News>){
        items.clear()
        items.addAll(updatedItems)
        notifyDataSetChanged()
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDate(dateStringUTC: String): Long{
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        var dateObject: Date? = null
        try {
            dateObject = simpleDateFormat.parse(dateStringUTC)
        }catch(e:ParseException){
            e.printStackTrace()
        }
        val df = SimpleDateFormat("MMM d, yyyy  h:mm a", Locale.getDefault())
        val formattedDateUTC = df.format(dateObject!!)

        df.timeZone = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        try {
            date = df.parse(formattedDateUTC)
            df.timeZone = TimeZone.getDefault()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date!!.time
    }

    private fun timeDifference(date: String): CharSequence{
        val currentTime = System.currentTimeMillis()
        val publicationDate = formatDate(date)

        return DateUtils.getRelativeTimeSpanString(publicationDate,currentTime,DateUtils.SECOND_IN_MILLIS)
    }

}