package com.strv.rapidioworkshop.ui

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.strv.rapidioworkshop.BR
import com.strv.rapidioworkshop.R
import com.strv.rapidioworkshop.databinding.FragmentChannelBinding
import com.strv.rapidioworkshop.model.Message
import com.strv.rapidioworkshop.rapid.RapidChat
import com.strv.rapidioworkshop.utils.vmb
import io.rapid.RapidDocument
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.DiffObservableList


interface ChannelView {
    val messageItemBinding: ItemBinding<RapidDocument<Message>>

}

class ChannelFragment : Fragment(), ChannelView {
    companion object {
        val ARG_CHANNEL_ID = "channel"

        fun newInstance(channelId: String) = ChannelFragment().apply {
            val args = Bundle()
            args.putString(ARG_CHANNEL_ID, channelId)
            arguments = args
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return vmb.rootView
    }


    val vmb = vmb<ChannelViewModel, FragmentChannelBinding>(R.layout.fragment_channel, { ChannelViewModel(arguments.getString(ARG_CHANNEL_ID)) })

    override val messageItemBinding = ItemBinding.of<RapidDocument<Message>>(BR.messageDoc, R.layout.item_message)
}

class ChannelViewModel(val channelId: String) : ViewModel() {

    val messages = DiffObservableList<RapidDocument<Message>>(object : DiffObservableList.Callback<RapidDocument<Message>> {
        override fun areItemsTheSame(oldItem: RapidDocument<Message>?, newItem: RapidDocument<Message>?) = oldItem == newItem
        override fun areContentsTheSame(oldItem: RapidDocument<Message>, newItem: RapidDocument<Message>) = oldItem.hasSameContentAs(newItem)
    })

    val newMessageText = ObservableField<String>("")

    init {
        RapidChat.MESSAGES.equalTo("channelId", channelId)
                .subscribe { messageDocs ->
                    messages.update(messageDocs)
                }
                .onError { error ->
                    error.printStackTrace()
                }
    }

    fun sendMessage() {
        RapidChat.MESSAGES.newDocument().mutate(Message(newMessageText.get(), channelId, "Kuba", System.currentTimeMillis()))
                .onError { error -> error.printStackTrace() }
                .onSuccess { newMessageText.set("") }
    }
}