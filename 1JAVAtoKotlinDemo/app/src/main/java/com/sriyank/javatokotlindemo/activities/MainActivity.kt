package com.sriyank.javatokotlindemo.activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import com.sriyank.javatokotlindemo.R
import com.sriyank.javatokotlindemo.app.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)


    }

    /** Save app username in SharedPreferences  */
    fun saveName(view: View) {
        if (isNotEmpty(etName, inputLayoutName)) {
            val personName = etName.text.toString()
            val sp = getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString(Constants.KEY_PERSON_NAME, personName)
            editor.apply()
        }
    }

    /** Search repositories on github  */
    fun listRepositories(view: View) {

        if (isNotEmpty(etRepoName, inputLayoutRepoName)) { //VaLidation Layer
            val queryRepo = etRepoName.text.toString()
            val repoLanguage = etLanguage.text.toString()

            val intent = Intent(this, DisplayActivity::class.java)
            intent.putExtra(Constants.KEY_QUERY_TYPE, Constants.SEARCH_BY_REPO)
            intent.putExtra(Constants.KEY_REPO_SEARCH, queryRepo)
            intent.putExtra(Constants.KEY_LANGUAGE, repoLanguage)

            startActivity(intent)
        }
    }

    /** Search repositories of a particular github user  */
    fun listUserRepositories(view: View) {
        if (isNotEmpty(etGithubUser, inputLayoutGithubUser)) { //VaLidation Layer
            val githubUser = etGithubUser.text.toString()

            val intent = Intent(this, DisplayActivity::class.java)
            intent.putExtra(Constants.KEY_QUERY_TYPE, Constants.SEARCH_BY_USER)
            intent.putExtra(Constants.KEY_GITHUB_USER, githubUser)
            startActivity(intent)
        }

    }

    /** Validation  */

    fun isNotEmpty(editText: EditText, textInputLayout: TextInputLayout): Boolean {

        if (editText.text.toString().isEmpty()) {
            textInputLayout.error = "Cannot be blank"
            return false
        } else {
            textInputLayout.isErrorEnabled = false
            return true
        }
    }

}