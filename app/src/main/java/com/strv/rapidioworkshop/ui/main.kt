package com.strv.rapidioworkshop.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.strv.rapidioworkshop.BR
import com.strv.rapidioworkshop.R
import com.strv.rapidioworkshop.databinding.ActivityMainBinding
import com.strv.rapidioworkshop.model.Channel
import com.strv.rapidioworkshop.rapid.RapidChat
import com.strv.rapidioworkshop.utils.SingleLiveData
import com.strv.rapidioworkshop.utils.vmb
import io.rapid.RapidDocument
import me.tatarka.bindingcollectionadapter2.ItemBinding

interface MainView {
    val channelItemBinding: ItemBinding<RapidDocument<Channel>>

}

interface ChannelClickedListener {
    fun onChannelClicked(channelDocId: String)
}

class MainActivity : AppCompatActivity(), MainView, ChannelClickedListener {
    val vmb = vmb<MainViewModel, ActivityMainBinding>(R.layout.activity_main)
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override val channelItemBinding =
            ItemBinding.of<RapidDocument<Channel>>(BR.channelDoc, R.layout.item_channel).bindExtra(BR.channelClickedListener, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDrawer()

        vmb.viewModel.displayMessage.observe(this, Observer {
            Snackbar.make(vmb.rootView, it!!, Snackbar.LENGTH_SHORT).show()
        })
    }

    override fun onChannelClicked(channelDocId: String) {
        supportFragmentManager.beginTransaction().replace(R.id.container, ChannelFragment.newInstance(channelDocId)).commit()
        vmb.binding.drawerLayout.closeDrawers()
        supportActionBar!!.title = "#" + channelDocId
    }

    private fun setupDrawer() {
        setSupportActionBar(vmb.binding.toolbar)
        val icon = ContextCompat.getDrawable(this, R.drawable.ic_menu)
        DrawableCompat.setTint(icon, ContextCompat.getColor(this, R.color.text))
        supportActionBar?.setHomeAsUpIndicator(icon)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerToggle = ActionBarDrawerToggle(this, vmb.binding.drawerLayout, R.string.open, R.string.close)
        vmb.binding.drawerLayout.addDrawerListener(drawerToggle)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }
}

class MainViewModel : ViewModel() {

    val displayMessage = SingleLiveData<String>()

    val newChannelName = ObservableField<String>("")

    val channels = ObservableField<List<RapidDocument<Channel>>>(ArrayList())

    init {
        RapidChat.CHANNELS
                .subscribe { channelDocs ->
                    channels.set(channelDocs)
                }
                .onError { error ->
                    error.printStackTrace()
                }
    }

    fun addChannel() {
        RapidChat.CHANNELS.document(newChannelName.get()).mutate(Channel(""))
                .onSuccess {
                    displayMessage.value = "Channel added."
                    newChannelName.set("")
                }
                .onError { error ->
                    error.printStackTrace()
                }
    }

}
