package com.example.contactmanagement;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
public class MainActivityData extends ViewModel {
    public MutableLiveData<Fragments> clickedValue;

    public MainActivityData()
    {
        clickedValue = new MediatorLiveData<Fragments>();
        clickedValue.setValue(Fragments.CONTACTLIST_FRAGMENT);
    }
    public Fragments getCurrentFragment()
    {
        return clickedValue.getValue();
    }
    public void changeFragment(Fragments value)
    {
        clickedValue.setValue(value);
    }
    public enum Fragments {
        CONTACTLIST_FRAGMENT,
        ADDCONTACTS_FRAGMENT,
        EDITCONTACTS_FRAGMENT,
    }
}
