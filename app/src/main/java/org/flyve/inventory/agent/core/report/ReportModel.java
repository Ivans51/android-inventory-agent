/*
 * Copyright Teclib. All rights reserved.
 *
 * Flyve MDM is a mobile device management software.
 *
 * Flyve MDM is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Flyve MDM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * ------------------------------------------------------------------------------
 * @author    Rafael Hernandez
 * @copyright Copyright Teclib. All rights reserved.
 * @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 * @link      https://github.com/flyve-mdm/android-mdm-agent
 * @link      https://flyve-mdm.com
 * ------------------------------------------------------------------------------
 */

package org.flyve.inventory.agent.core.report;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import org.flyve.inventory.InventoryTask;
import org.flyve.inventory.agent.R;
import org.flyve.inventory.agent.utils.Helpers;
import org.flyve.inventory.agent.utils.LocalPreferences;
import org.flyve.inventory.agent.utils.Utils;

import java.util.ArrayList;

public class ReportModel implements Report.Model {

    private Report.Presenter presenter;

    ReportModel(Report.Presenter presenter) {
        this.presenter = presenter;
    }

    public void generateReport(final Activity activity) {
        LocalPreferences preferences = new LocalPreferences(activity);
        ArrayList<String> listPreference = preferences.loadCategories();
        String description = Helpers.getAgentDescription(activity);
        final InventoryTask inventoryTask;
        if (listPreference.size() > 0) {
            listPreference.remove("");
            ArrayList<String> list = new ArrayList<>();
            for (String s : listPreference) {
                if (s.contains("STORAGES")) {
                    list.add("Storage");
                    continue;
                }
                if (s.contains("OPERATINGSYSTEM")) {
                    list.add("OperatingSystem");
                    continue;
                }
                if (s.contains("MEMORIES")) {
                    list.add("Memory");
                    continue;
                }
                if (s.contains("JVMS")) {
                    list.add("Jvm");
                    continue;
                }
                if (s.contains("SOFTWARES")) {
                    list.add("Software");
                    continue;
                }
                if (s.contains("USBDEVICES")) {
                    list.add("Usb");
                    continue;
                }
                if (s.contains("BATTERIES")) {
                    list.add("Battery");
                    continue;
                }
                list.add(s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase());
            }
            String[] categories = list.toArray(new String[list.size()]);
            inventoryTask = new InventoryTask(activity, description, true, categories);
        } else {
            inventoryTask = new InventoryTask(activity, description, true);
        }
        inventoryTask.getJSON(new InventoryTask.OnTaskCompleted() {
            @Override
            public void onTaskSuccess(String s) {
                presenter.sendInventory(s, Utils.loadJsonHeader(s));
                inventoryTask.getXMLSyn();
            }

            @Override
            public void onTaskError(Throwable throwable) {
                presenter.showError(throwable.getMessage());
            }
        });
    }

    public void showDialogShare(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_share_title);

        final int[] type = new int[1];

        //list of items
        String[] items = context.getResources().getStringArray(R.array.export_list);
        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        type[0] = which;
                    }
                });

        String positiveText = context.getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        Helpers.share( context, "Inventory Agent File", type[0] );
                    }
                });

        String negativeText = context.getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

}
