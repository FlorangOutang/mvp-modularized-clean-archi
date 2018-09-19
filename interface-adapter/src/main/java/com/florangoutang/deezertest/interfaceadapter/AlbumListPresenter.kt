package com.florangoutang.deezertest.interfaceadapter

import com.florangoutang.deezertest.entity.Album
import com.florangoutang.deezertest.usecase.AlbumListInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AlbumListPresenterImpl : AlbumListContract.Presenter {

    override var view: AlbumListContract.View? = null

    @Inject lateinit var interactor: AlbumListInteractor
    @Inject lateinit var transformer: AlbumListTransformer
    private val subscriptions: CompositeDisposable = CompositeDisposable()

    override fun attachView(view: AlbumListContract.View?) {
        this.view = view
    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun getAlbumList() {
        view?.showLoading(true)
        subscriptions.add(interactor.getAlbumList()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { view?.showLoading(false) }
                .subscribe({ albumList: List<Album> ->
                    view?.showAlbumList(albumList.map { transformer.albumToAlbumViewModel(it) }.toMutableList())
                },
                        { error: Throwable ->
                            error.printStackTrace()
                            view?.showAlbumListError()

                        }
                )
        )
    }

}