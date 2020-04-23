package com.dianascode.sweatworks.modules.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dianascode.sweatworks.R
import com.dianascode.sweatworks.models.User
import com.dianascode.sweatworks.modules.base.SweatWorksActivity
import com.dianascode.sweatworks.modules.home.HomeIntent.*
import com.dianascode.sweatworks.dialogs.SettingsFragmentDialog
import com.dianascode.sweatworks.modules.userDetail.UserDetailActivity
import com.dianascode.sweatworks.modules.userDetail.adapters.UserAdapter
import com.dianascode.sweatworks.mvibase.MviView
import com.dianascode.sweatworks.utils.SweatWorksViewModelFactory
import com.dianascode.sweatworks.utils.toJSON
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : SweatWorksActivity(), MviView<HomeIntent, HomeViewState> {

    // possible intents from user
    private val loadUsersIntentPublisher: PublishSubject<LoadUsersIntent> = PublishSubject.create()
    private val loadFavoriteUsersIntentPublisher: PublishSubject<LoadFavoriteUsersIntent> = PublishSubject.create()
    private val searchUserIntentPublisher: PublishSubject<SearchUserIntent> = PublishSubject.create()


    private val disposables = CompositeDisposable()
    private val viewModel: HomeViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this, SweatWorksViewModelFactory.getInstance(this))
            .get(HomeViewModel::class.java)
    }

    private val users = ArrayList<User>()
    private val favoriteUsers = ArrayList<User>()
    private lateinit var usersAdapter: UserAdapter
    private lateinit var favUsersAdapter: UserAdapter

    private lateinit var usersLayoutManager: GridLayoutManager
    private lateinit var favUsersLayoutManager: LinearLayoutManager

    private val limit = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        configureUI()
    }

    private fun configureUI() {
        usersAdapter = UserAdapter(users, this) { goToUserDetail(it) }
        favUsersAdapter = UserAdapter(favoriteUsers, this) { goToUserDetail(it) }

        usersLayoutManager = GridLayoutManager(this@HomeActivity, 6)
        favUsersLayoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)

        rvUsers.apply {
            layoutManager = usersLayoutManager
            adapter = usersAdapter
        }

        rvFavoriteUsers.apply {
            layoutManager = favUsersLayoutManager
            adapter = favUsersAdapter
        }


        rvUsers.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //recyclerView got scrolled vertically
                if(dy > 0) {
                    //check if we have reached the bottom of the recyclerView or not
                    //to do that we need to know how many items are there in the screen
                    //what is the top item position
                    //an recyclerView size
                    val visibleItemCount = usersLayoutManager.childCount
                    val pastVisibleItem = usersLayoutManager.findFirstCompletelyVisibleItemPosition()
                    val total = usersAdapter.itemCount

                    //if is not loading, we can get the next page data
                    if(progressBar.visibility != View.VISIBLE) {
                        //check if we reached the bottom or not
                        if((visibleItemCount + pastVisibleItem) >= total) {
                            loadUsersIntentPublisher.onNext(LoadUsersIntent(limit))
                        }
                    }


                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })



    }

    private fun goToUserDetail(user: User) {
        val i = Intent(this, UserDetailActivity::class.java)
        i.putExtra(UserDetailActivity.USER_SELECTED, user)
        startActivityForResult(i, REQUEST_CODE_USER_DETAIL)
    }

    override fun intents(): Observable<HomeIntent> {
        return Observable.mergeArray(
            loadUsersIntent(),
            loadFavoriteUsersIntent(),
            searchUserIntent()
        )
    }

    override fun render(state: HomeViewState) {
        Log.d(TAG, "State: $state")
        if(state.isProcessing) {
            progressBar.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.GONE
        }

        if(state.error != null) {
            handleError(state.error)
            return
        }


        if(state.searching) {
            if (state.usersMatch.toJSON() != users.toJSON()) {
                updateUsersList(state.usersMatch)
            }

            if (state.usersMatch.toJSON() != favoriteUsers.toJSON()) {
                updateFavoriteUsersList(state.favoriteUsersMatch)
            }
        }else {

            if (state.favoriteUsers.toJSON() != favoriteUsers.toJSON()) {
                updateFavoriteUsersList(state.favoriteUsers)
            }

            if (state.users.toJSON() != users.toJSON()) {
                updateUsersList(state.users)
            }
        }

    }

    private fun updateUsersList(newList: List<User>) {
        users.clear()
        users.addAll(newList)
        usersAdapter.notifyDataSetChanged()
    }

    private fun updateFavoriteUsersList(newList: List<User>) {
        if (newList.isEmpty()) {
            tvFavoriteUsers.visibility = View.GONE
            rvFavoriteUsers.visibility = View.GONE
        } else {
            tvFavoriteUsers.visibility = View.VISIBLE
            rvFavoriteUsers.visibility = View.VISIBLE
        }

        favoriteUsers.clear()
        favoriteUsers.addAll(newList)
        favUsersAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        bind()

        if(users.isEmpty()) //only load the first time
            loadUsersIntentPublisher.onNext(LoadUsersIntent(limit))

        if(favoriteUsers.isEmpty()) // only load the first time
            loadFavoriteUsersIntentPublisher.onNext(LoadFavoriteUsersIntent)
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    private fun bind() {
        disposables.add(viewModel.states().subscribe(this::render))
        viewModel.processIntents(intents())
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_USER_DETAIL && resultCode == Activity.RESULT_OK) {
            loadFavoriteUsersIntentPublisher.onNext(LoadFavoriteUsersIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        (menu?.findItem(R.id.searchItem)?.actionView as SearchView).apply {
            this.queryHint= "Search"
            setIconifiedByDefault(false)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {

                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { searchUserIntentPublisher.onNext(SearchUserIntent(it, true)) }
                    newText?.let { searchUserIntentPublisher.onNext(SearchUserIntent(it, false)) }

                    return true
                }

            })
            return super.onCreateOptionsMenu(menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.settingsItem -> {
                val settingsFragment = SettingsFragmentDialog()
                settingsFragment.show(supportFragmentManager, "SettingsFragment")
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun loadUsersIntent(): Observable<LoadUsersIntent> = loadUsersIntentPublisher
    private fun loadFavoriteUsersIntent(): Observable<LoadFavoriteUsersIntent> = loadFavoriteUsersIntentPublisher
    private fun searchUserIntent(): Observable<SearchUserIntent> = searchUserIntentPublisher


    companion object {
        private const val TAG = "HomeActivity"
        private const val REQUEST_CODE_USER_DETAIL = 101
    }
}
