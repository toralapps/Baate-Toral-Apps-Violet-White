package com.nimychat.bottlewhite.videocall.activitys

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.app.ads.NativAddsFragment
import com.nimychat.bottlewhite.videocall.databinding.FragmentLeaveBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeavScreenDailog: NativAddsFragment() {

    lateinit var binding: FragmentLeaveBinding

    override val nativeAdLayout: LinearLayout?
        get() = binding.bannerContainer
    override val adContainer: LinearLayout?
        get() = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentLeaveBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.yesbtn.setOnClickListener {
            requireActivity().finish()
        }
        binding.nobtn.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        fun newInstance(): LeavScreenDailog {
            return LeavScreenDailog()
        }
    }
}