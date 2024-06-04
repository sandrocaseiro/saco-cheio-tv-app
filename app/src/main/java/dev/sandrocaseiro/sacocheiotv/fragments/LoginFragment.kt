package dev.sandrocaseiro.sacocheiotv.fragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import dev.sandrocaseiro.sacocheiotv.Constants
import dev.sandrocaseiro.sacocheiotv.MainActivity
import dev.sandrocaseiro.sacocheiotv.R
import dev.sandrocaseiro.sacocheiotv.models.viewmodels.LoginViewModel

class LoginFragment : Fragment() {

    private val vm = LoginViewModel()
    private lateinit var mPrefs: SharedPreferences

    private lateinit var mLoginInput: EditText
    private lateinit var mPasswordInput: EditText
    private lateinit var mLoginButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        mPrefs = requireActivity().getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE)

        mLoginInput = view.findViewById(R.id.etLogin)
        mPasswordInput = view.findViewById(R.id.etPassword)
        mLoginButton = view.findViewById(R.id.btLogin)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()

        mLoginButton.setOnClickListener(object : OnClickListener {
            override fun onClick(view: View?) {
                if (mLoginInput.text.isNullOrEmpty() || mPasswordInput.text.isNullOrEmpty()) {
                    Log.d(TAG, "Empty credentials")
                    Toast.makeText(requireContext(), "Please input your credentials", Toast.LENGTH_SHORT).show()
                    return
                }

                vm.authenticate(mLoginInput.text.toString(), mPasswordInput.text.toString())
            }
        })
    }

    private fun observeViewModel() {
        vm.authHash.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Invalid credentials", Toast.LENGTH_SHORT).show()
                return@observe
            }

            mPrefs.edit()
                .putString(Constants.AUTH_HASH, it)
                .apply()

            val intent = Intent(requireContext(), MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}
