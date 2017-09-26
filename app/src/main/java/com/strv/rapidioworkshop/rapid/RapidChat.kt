package com.strv.rapidioworkshop.rapid

import com.strv.rapidioworkshop.model.Channel
import com.strv.rapidioworkshop.model.Message
import io.rapid.Rapid


object RapidChat {
    val CHANNELS
        get() = Rapid.getInstance().collection("channels", Channel::class.java)

    val MESSAGES
        get() = Rapid.getInstance().collection("messages", Message::class.java)
}