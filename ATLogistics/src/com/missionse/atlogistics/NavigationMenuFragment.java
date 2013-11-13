package com.missionse.atlogistics;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NavigationMenuFragment extends ListFragment {
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.navigation_menu_list, null);
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		createMenu();
	}

	private void createMenu() {
		final List<String> menuItems = new ArrayList<String>();
		menuItems.add("MAP");
		menuItems.add("RESOURCE FINDER");

		setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.navigation_menu_entry, R.id.navigation_menu_text,
				menuItems));
	}

	@Override
	public void onListItemClick(final ListView listView, final View view, final int position, final long id) {
		ATLogistics activity = (ATLogistics) getActivity();

		String selectedItem = (String) listView.getAdapter().getItem(position);
		if (selectedItem.equals("MAP")) {
			activity.showLeftMap();
		} else if (selectedItem.equals("RESOURCE FINDER")) {
			activity.showResourceFinder();
		}
	}
}
