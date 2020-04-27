package com.example.prova_tirocinio.objects;

import java.util.ArrayList;
import java.util.List;

/**
    Classe che rappresenta i device che posso trovare nella scansione, usata per provare
    recyclerview con più view, in futuro separazione in vari oggetti
 */


public class Device {

    private String name;
    private int type;


    public Device(String name, int type){
        this.name=name;
        this.type=type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public static List<Device> get10DevicesForTesting(){
        //1 Thingy, 2 Wagoo, 3 SmartWatch

        List <Device> list =new ArrayList<>();

        list.add(new Device("Thingy1", 1));
        list.add(new Device("Wagoo2", 2));
        list.add(new Device("Watch3", 3));
        list.add(new Device("Thingy4", 1));
        list.add(new Device("Wagoo5", 2));
        list.add(new Device("Watch6", 3));
        list.add(new Device("Thingy7", 1));
        list.add(new Device("Wagoo8", 2));
        list.add(new Device("Watch9", 3));
        list.add(new Device("Thingy10", 1));

        return list;

    }

}
