package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            Ping ping = new Ping("8.8.8.8");
            ping.continuousConnectionCheck();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
