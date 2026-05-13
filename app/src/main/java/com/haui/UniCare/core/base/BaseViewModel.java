package com.haui.UniCare.core.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class BaseViewModel extends ViewModel {

    // Trạng thái bật/tắt loading
    protected final MutableLiveData<Boolean> showLoading = new MutableLiveData<>(false);

    // Trạng thái lỗi để View lắng nghe
    protected final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Boolean> getShowLoading() {
        return showLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}