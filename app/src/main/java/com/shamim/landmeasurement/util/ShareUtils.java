package com.shamim.landmeasurement.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.shamim.landmeasurement.R;
import com.shamim.landmeasurement.recycle_view.NewsAdapter;
import java.io.File;
import java.io.FileWriter;

public class ShareUtils {

  /**
   * Shows a dialog with options to share as plain text or as a text file.
   *
   * @param context Context
   * @param shareableText The text content to be shared
   * @param fileName File Name
   */
  public static void showShareOptionsDialog(
      Context context, String shareableText, String fileName) {
    if (shareableText == null || shareableText.trim().isEmpty()) {
      Toast.makeText(context, R.string.no_result_to_share, Toast.LENGTH_SHORT).show();
      return;
    }

    String[] titles = {
      context.getString(R.string.share_as_text), context.getString(R.string.share_as_file)
    };
    int[] icons = {R.drawable.text_ad_24px, R.drawable.file_export_24px};

    NewsAdapter adapter =
        new NewsAdapter(
            titles,
            icons,
            position -> {
              if (position == 0) {
                shareAsPlainText(context, shareableText);
              } else if (position == 1) {
                shareAsTextFile(context, shareableText, fileName);
              }
            });

    androidx.recyclerview.widget.RecyclerView recyclerView =
        new androidx.recyclerview.widget.RecyclerView(context);
    recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(context));
    recyclerView.setAdapter(adapter);
    recyclerView.setPadding(30, 30, 30, 30);

    new MaterialAlertDialogBuilder(context)
        .setTitle(context.getString(R.string.share_choose_option))
        .setView(recyclerView)
        .setPositiveButton(R.string.close, null)
        .show();
  }

  /** Share content as plain text */
  public static void shareAsPlainText(Context context, String text) {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_title));
    shareIntent.putExtra(Intent.EXTRA_TEXT, text);

    context.startActivity(
        Intent.createChooser(shareIntent, context.getString(R.string.share_title)));
  }

  /** Share content as a .txt file */
  public static void shareAsTextFile(Context context, String text, String fileName) {
    try {
      File file = new File(context.getCacheDir(), fileName + ".txt");
      try (FileWriter writer = new FileWriter(file)) {
        writer.write(text);
      }

      Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

      Intent shareIntent = new Intent(Intent.ACTION_SEND);
      shareIntent.setType("text/plain");
      shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
      shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

      context.startActivity(
          Intent.createChooser(shareIntent, context.getString(R.string.share_title)));

    } catch (Exception e) {
      Toast.makeText(
              context,
              context.getString(R.string.error_cannot_create_file) + ": " + e.getMessage(),
              Toast.LENGTH_SHORT)
          .show();
    }
  }
}
