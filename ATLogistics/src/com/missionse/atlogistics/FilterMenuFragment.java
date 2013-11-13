package com.missionse.atlogistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.missionse.atlogistics.resources.ResourceType;

public class FilterMenuFragment extends Fragment {
	//private final List<String> menuItems = new ArrayList<String>();
	private ListView listView;

	private Button showAllButton;
	private Button hideAllButton;

	private HashMap<String, CheckBox> checkboxes = new HashMap<String, CheckBox>();

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		createMenu();
	}

	private void createMenu() {
		List<ResourceType> resourceTypes = new ArrayList<ResourceType>(Arrays.asList(ResourceType.values()));
		listView.setAdapter(new FilterAdapter(getActivity(), R.layout.filter_menu_entry, resourceTypes));
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.filter_menu_list, null);
		listView = (ListView) view.findViewById(R.id.filter_list);

		showAllButton = (Button) view.findViewById(R.id.show_all_button);
		showAllButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View arg0) {
				for (Entry<String, CheckBox> entry : checkboxes.entrySet()) {
					entry.getValue().setChecked(true);
				}
			}
		});
		hideAllButton = (Button) view.findViewById(R.id.hide_all_button);
		hideAllButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View arg0) {
				for (Entry<String, CheckBox> entry : checkboxes.entrySet()) {
					entry.getValue().setChecked(false);
				}
			}
		});

		return view;
	}

	private class FilterAdapter extends ArrayAdapter<ResourceType> {

		private int listItemResource;
		private List<ResourceType> resourceTypes;

		public FilterAdapter(final Context context, final int resource, final List<ResourceType> types) {
			super(context, resource, types);
			listItemResource = resource;
			resourceTypes = types;
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(listItemResource, null);
			}

			ResourceType resource = resourceTypes.get(position);
			String textLabel = resource.getDescription();
			if (textLabel != null) {
				ImageView icon = (ImageView) convertView.findViewById(R.id.filter_menu_icon);
				CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.filter_menu_text);

				if (icon != null && checkBox != null) {
					icon.setImageResource(resource.getResourceId());
					checkBox.setText(textLabel);
					checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
							FilterMenuFragment.this.onCheckboxStateChange(buttonView.getText().toString(), isChecked);
						}
					});

					checkboxes.put(textLabel, checkBox);
				}
			}
			return convertView;
		}
	}

	public void onCheckboxStateChange(final String item, final boolean isChecked) {
		Log.e("FilterMenu", "item: " + item + " is now " + isChecked);
	}
}
