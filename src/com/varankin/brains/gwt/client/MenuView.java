package com.varankin.brains.gwt.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;

/**
 *
 * @author nvara
 */
public class MenuView extends MenuBar
{

    public MenuView()
    {
        super( false );
        
        // Make a command that we will execute from all leaves.
        Command cmd = new Command() {
          public void execute() {
            Window.alert("You selected a menu item!");
          }
        };

        // Make some sub-menus that we will cascade from the top menu.
        MenuBar brains = new MenuBar( true );
        brains.addItem("the", cmd);
        brains.addItem("foo", cmd);
        brains.addItem("menu", cmd);

        MenuBar process = new MenuBar( true );
        process.addItem("the", cmd);
        process.addItem("bar", cmd);
        process.addItem("menu", cmd);

        MenuBar tools = new MenuBar( true );
        tools.addItem("the", cmd);
        tools.addItem("baz", cmd);
        tools.addItem("menu", cmd);

        MenuBar help = new MenuBar( true );
        help.addItem("the", cmd);
        help.addItem("baz", cmd);
        help.addItem("About", cmd);

        // Make a new menu bar, adding a few cascading menus to it.
        addItem( "Brains", brains );
        addItem( "Process", process );
        addItem( "Tools", tools );
        addItem( "Help", help );
    }
}
