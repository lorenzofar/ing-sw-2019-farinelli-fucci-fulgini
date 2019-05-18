package it.polimi.deib.se2019.sanp4.adrenaline.client.gui;

import it.polimi.deib.se2019.sanp4.adrenaline.client.ClientView;

abstract class GUIController {
    ClientView clientView;

    /**
     * Set the provided client view
     * @param clientView The object representing the client view, not null
     */
    void setClientView(ClientView clientView){
        if(clientView == null){
            throw new NullPointerException("Client view cannot be null");
        }
        this.clientView = clientView;
    }
}