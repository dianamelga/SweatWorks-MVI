package com.dianascode.sweatworks.modules.userDetail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.dianascode.sweatworks.R
import com.dianascode.sweatworks.models.User
import com.dianascode.sweatworks.modules.base.SweatWorksActivity
import com.dianascode.sweatworks.modules.userDetail.UserDetailIntent.*
import com.dianascode.sweatworks.mvibase.MviView
import com.dianascode.sweatworks.utils.SweatWorksViewModelFactory
import com.dianascode.sweatworks.utils.UtilTools
import com.dianascode.sweatworks.utils.asFormattedDate
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_user_detail.*
import kotlinx.android.synthetic.main.activity_user_detail.tvTitle
import kotlinx.android.synthetic.main.toolbar_view.*

class UserDetailActivity : SweatWorksActivity(), MviView<UserDetailIntent, UserDetailViewState> {

    private val addToFavoritesIntentPublisher: PublishSubject<AddToFavoritesIntent> = PublishSubject.create()
    private val removeFromFavoritesIntentPublisher: PublishSubject<RemoveFromFavoritesIntent> = PublishSubject.create()
    private val isFavoriteIntentPublisher: PublishSubject<IsFavoriteIntent> = PublishSubject.create()

    private val disposables = CompositeDisposable()
    private val viewModel: UserDetailViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this, SweatWorksViewModelFactory.getInstance(this))
            .get(UserDetailViewModel::class.java)
    }
    private lateinit var user: User
    private var isFavorite: Boolean = false
    private var isFirstRender: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            adaptViewForInsets()
            requestToBeLayoutFullscreen()
        }

        configureUI()
        configureClickListeners()
    }

    private fun configureUI() {
        setSupportActionBar(appToolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        tvToolbarTitle.text = "SweatWorks"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        user = intent.extras?.getSerializable(USER_SELECTED) as User
        user.apply {
            tvTitle.text = name?.title?.capitalize()
            tvName.text = name?.first?.capitalize() + " " + name?.last?.capitalize()

            tvMail.text = email
            tvPhone.text = phone
            tvAddress.text = location?.city + " - " + location?.state + " / " + location?.street?.name + " " + location?.street?.number
            tvDob.text = dob?.date?.asFormattedDate()

            Picasso.get()
                .load(picture?.large)
                .into(ivUser)

            if ("female" == gender?.toLowerCase()) {
                ivUser.borderColor = ContextCompat.getColor(this@UserDetailActivity, R.color.pinkFemale)
            } else {
                ivUser.borderColor = ContextCompat.getColor(this@UserDetailActivity, R.color.blueMale)
            }
        }

    }



    private fun configureClickListeners() {
        clPhone.setOnClickListener {
            if (!UtilTools.hasPermission(Manifest.permission.WRITE_CONTACTS, this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_CONTACTS),
                        PERMISSION_REQUEST_CODE_WRITE_CONTACTS
                    )
                }
            } else {
                addAsContactConfirmed(user)
            }
        }

        btFavorite.setOnClickListener {
            if(isFavorite) {
                removeFromFavoritesIntentPublisher.onNext(RemoveFromFavoritesIntent(user))
            }else {
                addToFavoritesIntentPublisher.onNext(AddToFavoritesIntent(user))
            }
            setResult(RESULT_OK)
        }
    }

    private fun addAsContactConfirmed(person: User) {

        val intent = Intent(Intent.ACTION_INSERT)
        intent.type = ContactsContract.Contacts.CONTENT_TYPE
        intent.putExtra(
            ContactsContract.Intents.Insert.NAME, person.name?.first?.toLowerCase()?.capitalize() +
                    " " + person.name?.last?.toLowerCase()?.capitalize()
        )
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, person.phone)
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, person.email)

        startActivity(intent)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE_WRITE_CONTACTS -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        addAsContactConfirmed(user)
                    }
                }
            }
        }
    }


    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    override fun onStart() {
        super.onStart()
        bind()

        isFavoriteIntentPublisher.onNext(IsFavoriteIntent(user))
    }

    private fun bind() {
        disposables.add(viewModel.states().subscribe(this::render))
        viewModel.processIntents(intents())
    }

    override fun intents(): Observable<UserDetailIntent> {
        return Observable.mergeArray(
            addToFavoritesIntent(),
            removeFromFavoritesIntent(),
            isFavoriteIntent()
        )
    }

    override fun render(state: UserDetailViewState) {
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

        if(state.isFavorite != isFavorite) {
            if (state.isFavorite) {
                btFavorite.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_passion_filled
                    )
                )

                if(!isFirstRender) {
                    Toast.makeText(this, getString(R.string.saved_as_favorite), Toast.LENGTH_SHORT)
                        .show()
                }else{
                    isFirstRender = false
                }

                isFavorite = state.isFavorite
            } else {
                btFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_passion))

                if(!isFirstRender) {
                    Toast.makeText(
                        this,
                        getString(R.string.removed_from_favorites),
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    isFirstRender = false
                }
                isFavorite = state.isFavorite
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    private fun adaptViewForInsets() {
        val toolbarPaddingTop = toolbar.paddingTop
        // Register OnApplyWindowInsetsListener
        window?.decorView?.setOnApplyWindowInsetsListener { _, windowInsets ->
            // Update toolbar's top padding to accommodate system window top inset
            val newToolbarTopPadding =
                windowInsets.systemWindowInsetTop + toolbarPaddingTop
            toolbar.updatePadding(top = newToolbarTopPadding)

            // Update layout's bottom padding to accommodate
            // system window bottom inset
            //coordLayout.updatePadding(bottom = windowInsets.systemWindowInsetBottom)

            windowInsets
        }
    }


    private fun addToFavoritesIntent(): Observable<AddToFavoritesIntent> = addToFavoritesIntentPublisher
    private fun removeFromFavoritesIntent(): Observable<RemoveFromFavoritesIntent> = removeFromFavoritesIntentPublisher
    private fun isFavoriteIntent(): Observable<IsFavoriteIntent> = isFavoriteIntentPublisher


    companion object {
        private const val TAG = "UserDetailActivity"
        const val USER_SELECTED = "user_selected"
        const val PERMISSION_REQUEST_CODE_WRITE_CONTACTS = 100
    }
}
