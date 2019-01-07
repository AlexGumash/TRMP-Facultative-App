package alexey.com.facultativeapp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tomash.androidcontacts.contactgetter.entity.ContactData;
import com.tomash.androidcontacts.contactgetter.main.contactsGetter.ContactsGetterBuilder;

import java.util.ArrayList;
import java.util.List;

import alexey.com.facultativeapp.R;
import alexey.com.facultativeapp.adapters.ContactsListAdapter;

public class FragmentContacts extends Fragment {

    /*
    Фрагмент со списком контактов
     */

    private RecyclerView contactRecyclerView;
    private LinearLayoutManager layoutManager;
    private ContactsListAdapter contactsListAdapter;
    private List<ContactData> contactDataList = new ArrayList<>();

    /*
    Создание View по файлу fragment_contacts
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        contactRecyclerView = view.findViewById(R.id.contactRecyclerView);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        contactRecyclerView.setLayoutManager(layoutManager);
        contactsListAdapter = new ContactsListAdapter(contactDataList);
        contactRecyclerView.setAdapter(contactsListAdapter);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        permissionsRequest();
    }

    /*
    Отрисовка контактов
     */
    private void showContacts() {
        contactDataList.clear();
        contactDataList.addAll(new ContactsGetterBuilder(getContext())
                .allFields()
                .buildList());
        contactsListAdapter.notifyDataSetChanged();
    }

    /*
    Запрос на проверку необходимых разрешений
     */
    private void permissionsRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED ) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 1);
        } else {
            showContacts();
        }
    }


    /*
    Если разрешения получены, то отобразить контакты.
    Если нет, то показать сообщение о том, что необходимо получить разрешения
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we cannot display the " +
                        "names", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
