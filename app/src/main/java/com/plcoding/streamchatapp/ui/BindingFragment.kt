package com.plcoding.streamchatapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint

abstract class BindingFragment<out T : ViewBinding> : Fragment() {

    private var _binding: ViewBinding? = null
    @Suppress("UNCHECKED_CAST")
    protected val binding: T
        get() = _binding as T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = bindingInflater(inflater)
        return _binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected abstract val bindingInflater: (LayoutInflater) -> ViewBinding
}