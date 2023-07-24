package com.example.onlinestore.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.service.autofill.OnClickAction
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.example.onlinestore.R
import com.example.onlinestore.databinding.ActivityMainBinding
import com.example.onlinestore.product.Product
import com.example.onlinestore.product.ProductAdapter
import com.example.onlinestore.product.ProductDetailsActivity
import com.example.onlinestore.product.ProductResponse
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var products = ArrayList<Product>()
    private var productAdapter: ProductAdapter? = null
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var checkNetworkConnection: CheckNetworkConnection


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VolleySingleton.initializeRequestQueue(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textView = findViewById(R.id.textView)
        callNetworkConnection()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.nav_home -> Toast.makeText(
                    applicationContext,
                    "Clicked Home",
                    Toast.LENGTH_LONG
                ).show()
                R.id.nav_message -> Toast.makeText(
                    applicationContext,
                    "Clicked Message",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_sync -> Toast.makeText(
                    applicationContext,
                    "Clicked Sync",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_trash -> Toast.makeText(
                    applicationContext,
                    "Clicked Trash",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_setting -> Toast.makeText(
                    applicationContext,
                    "Clicked Setting",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_login -> Toast.makeText(
                    applicationContext,
                    "Clicked Login",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_share -> Toast.makeText(
                    applicationContext,
                    "Clicked Share",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_rate -> Toast.makeText(
                    applicationContext,
                    "Clicked Rate Us",
                    Toast.LENGTH_SHORT
                ).show()
            }
            true
        }


        CoroutineScope(Dispatchers.IO).launch {

            productAdapter = ProductAdapter(products)
            CoroutineScope(Dispatchers.Main).launch {
                initRecyclerView()
            }

        }
        this.title = "Home"
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))

            when (item.itemId) {
                R.id.nan_delete -> Toast.makeText(this, "Delete Selected", Toast.LENGTH_LONG).show()
                R.id.nan_favourite -> Toast.makeText(this, "Favourite Selected", Toast.LENGTH_LONG)
                    .show()
            }

        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        binding.RecyclerView.adapter = productAdapter
        binding.RecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.RecyclerView.adapter?.notifyDataSetChanged()
        binding.RecyclerView.setOnClickListener {
            val intent = Intent(this@MainActivity,ProductDetailsActivity::class.java)
            startActivity(intent)
        }

    }

    private fun jsonObjectRequest() {
        var volleyJsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            "https://dummyjson.com/products",
            null,
            JsonObjectRequestSuccessListener(),
            StringRequestErrorListener()
        )
        VolleySingleton.volleyRequestQueue!!.add(volleyJsonObjectRequest)
    }

    private fun callNetworkConnection() {
        var checkNetworkConnection = CheckNetworkConnection(application)
        checkNetworkConnection.observe(this) { isConnected ->
            if (isConnected) {
                Handler(Looper.getMainLooper()).postDelayed({}, 1000).run {
                    jsonObjectRequest()
                    Toast.makeText(applicationContext, "Back Online", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                Toast.makeText(applicationContext, "No Internet", Toast.LENGTH_LONG)
                    .show()
            }

        }

    }

    inner class JsonObjectRequestSuccessListener : Response.Listener<JSONObject> {
        override fun onResponse(response: JSONObject?) {
            var productResponse = Gson().fromJson<ProductResponse>(
                response.toString(),
                ProductResponse::class.java
            )
            products.addAll(productResponse.products)
            productAdapter?.notifyDataSetChanged()
        }

    }

    class StringRequestErrorListener : Response.ErrorListener {
        override fun onErrorResponse(error: VolleyError?) {
            Log.e("tag", "$error")
        }
    }

    companion object {

    }
}
