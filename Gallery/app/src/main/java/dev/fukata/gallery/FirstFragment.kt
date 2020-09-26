package dev.fukata.gallery

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import androidx.recyclerview.widget.GridLayoutManager as GridLayoutManager

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    lateinit var media: ArrayList<File>
    lateinit var galleryView: RecyclerView
    lateinit var viewAdapter: GalleryAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.media = ArrayList()
        refreshMedia()

        val viewManager = GridLayoutManager(context, 4)
        this.viewAdapter = GalleryAdapter(this.media)
        this.galleryView = view.findViewById<RecyclerView>(R.id.gallery).apply {
            setHasFixedSize(false)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    fun onDataChanged() {
        Log.d("FirstFragment", "onDataChanged")
        refreshMedia()
        Handler(Looper.getMainLooper()).postDelayed({
            this.viewAdapter.notifyDataSetChanged()
        }, 0)
    }

    fun refreshMedia() {
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (storageDir != null) {
            val files = storageDir.listFiles().filter { file ->
                file.isFile && file.length() > 0
            }.sortedByDescending { it.lastModified() }
            this.media.clear()
            this.media.addAll(files)
        }
    }
}

class GalleryAdapter(private val media: List<File>) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {
    val thumbnailCaches = HashMap<String, Bitmap>()

    class GalleryViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val imageView = ImageView(parent.context).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
        }
        return GalleryViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.itemView.post({
            setMedium(holder.imageView, media[position])
        })
    }

    override fun getItemCount(): Int {
        return media.size
    }

    private fun setMedium(imageView: ImageView, file: File) {
        Log.d("Gallery", "file=${file.absoluteFile}")
        // Get the dimensions of the View
        val targetW: Int = imageView.width
        val targetH: Int = imageView.width

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            val photoW: Int = 3000
            val photoH: Int = 4000

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)
            Log.d("Gallery", "scaleFactor=${scaleFactor}, photo=${photoW}/${photoH}, target=${targetW}/${targetH}")

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        if (thumbnailCaches.containsKey(file.absolutePath)) {
            imageView.setImageBitmap(thumbnailCaches.get(file.absolutePath))
        } else {
            BitmapFactory.decodeFile(file.absolutePath, bmOptions)?.also { bitmap ->
                imageView.setImageBitmap(bitmap)
                this.thumbnailCaches.set(file.absolutePath, bitmap)
            }
        }
    }
}
