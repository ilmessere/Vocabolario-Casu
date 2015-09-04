package com.mess.vocabolariocasu;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mess.vocabolariocasu.vocabolario.VocabolarioCasuDatabase;
import com.mess.vocabolariocasu.word.WordContent;

import static android.text.Html.fromHtml;

/**
 * A fragment representing a single Word detail screen.
 * This fragment is either contained in a {@link WordListActivity}
 * in two-pane mode (on tablets) or a {@link WordDetailActivity}
 * on handsets.
 */
public class WordDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    //private DummyContent.DummyItem mItem;
    private Cursor cursor;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WordDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //mItem = DummyContent.get(getArguments().getString(ARG_ITEM_ID));
            cursor = WordContent.getItem(getArguments().getString(ARG_ITEM_ID), getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_word_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                ((TextView) rootView.findViewById(R.id.word_detail))
                        .setText(fromHtml(cursor.getString(
                                        cursor.getColumnIndexOrThrow(VocabolarioCasuDatabase.KEY_DEFINITION)))
                        );
                getActivity().setTitle(cursor.getString(
                        cursor.getColumnIndexOrThrow(VocabolarioCasuDatabase.KEY_WORD)
                )
                );
            }
        }

        return rootView;
    }
}
