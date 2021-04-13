package com.hyc.backend.thread;

/**
 * @author kol Huang
 * @date 2021/4/13
 */
public class IDSThread extends Thread{


    public IDSThread(int priority){
        if(priority < 1)    priority = 1;
        if(priority > 10)   priority = 10;
        setPriority(priority);
    }

    @Override
    public void run() {

    }
}
