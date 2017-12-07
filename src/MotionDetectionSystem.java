import org.omg.CORBA.FREE_MEM;
//import sun.jvm.hotspot.jdi.BooleanTypeImpl;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by constie on 01.12.2017.
 */
public class MotionDetectionSystem implements MotionDetectionSystemInterface {
    private TreeMap<Integer, Boolean> frameStatuses = new TreeMap<>();
    private TreeMap<Integer, int[][]> frameImages = new TreeMap<>();
//    private Map<Integer, Point2D.Double> results = Collections.synchronizedMap(new TreeMap<>());
    private ImageConverterInterface imgConvrt;
    private ResultConsumerInterface rcinter;
    private Point2D.Double imgResults;
    private int threadsNo = 0;
//    private int iter = 0;
    private AtomicInteger iter = new AtomicInteger(0);
    private List<Integer> pairsList = Collections.synchronizedList(new ArrayList<>());
    private Map<Integer, Point2D.Double> results = Collections.synchronizedMap(new TreeMap<>());
    private Map<Integer, Thread> threadsArray = new TreeMap<>();

    @Override
    public void setThreads(int threads) {
        threadsNo = threads;
//        threads to ilosc watkow ktora ma wykonywac prace
//        w trakcie pracy programu moze sie zmieniac:
//        -  zwiekszenie liczby watkow - jak najszybsze zagospodarownaie
//        -  zmienjszenie liczby watkow - stopniowe ich wygaszanie

    }

    @Override
    public void setImageConverter(ImageConverterInterface ici) {
        imgConvrt = ici;

//        jdenokrotne wykonanie przed pierwszym dostarczeniem obrazu
//        przekazanie referencji do obiektu odpowiedzialnego za przetwarzanie obrazu

        for(int i=0; i<threadsNo; i++) {
            threadsArray.put(i+1,
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (pairsList) {
//                        if(pairsList.isEmpty()) {
                            try {
                                pairsList.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
//                        }
                    }

                    while (!pairsList.isEmpty()) {

                        AtomicInteger frameNo =  new AtomicInteger(0);
//                        int dframeNo = 0;
                        int[][] img1 = null;
                        int[][] img2 = null;

                        synchronized (pairsList) {
                            frameNo.set(pairsList.get(0));
                            PMO_SystemOutRedirect.println("-----------------------: " + frameNo.get() );
                            pairsList.remove(0);
                        }
                        synchronized (frameImages){
                            img1 = frameImages.get(frameNo.get());
                            img2 = frameImages.get(frameNo.incrementAndGet());
                        }
//                        synchronized (frameImages){
//                            one = frameImages.get(frameNo);
//                            two = frameImages.get(frameNo+1);
//                        }
//                        PMO_SystemOutRedirect.println("frameNoO: " + frameNo + " frameNoT: " + dframeNo + " frameImages,size(): " + frameImages.lastKey());
                        PMO_SystemOutRedirect.println("frameImages.get(frameNo): " + frameNo.get() );
//                        PMO_SystemOutRedirect.println("one: " + one + " two: " + two);
                        if(img2 != null) {
                            Point2D.Double result = imgConvrt.convert(frameNo.get(), img1, img2);
                            results.put(frameNo.decrementAndGet(), result);

                            sendResults();
                        }

                    }


                }
            }));
            threadsArray.get(i+1).start();


        }

    }

    @Override
    public void setResultListener(ResultConsumerInterface rci) {
        rcinter = rci;

//        jdenokrotne wykonanie przed pierwszym dostarczeniem obrazu
//        przekazywanie wynikow do listenera

    }

      synchronized public void sendResults(){
        PMO_SystemOutRedirect.println("results.get(iter): " + results.get(iter.get()));
//         for(int u=0; u<10; u++){
//             PMO_SystemOutRedirect.println("threadsArray.get(u+1).getState(): " + threadsArray.get(u+1).getName() + " " +threadsArray.get(u+1).getState());
//         }
        while(results.get(iter.get()) != null){
            rcinter.accept(iter.get(), results.get(iter.get()));
            results.remove(iter.get());
            iter.incrementAndGet();
        }
        return;
    }

    @Override
    public void addImage(int frameNumber, int[][] image) {

//        przekazywanie obrazu do przetworzenia
//        wg frameNumber (od 0 wlacznie)
//        obrazy moga byc przekazywanie w dowolnej kolejnosci
//        wszystkie obrazy maja ten sam rozmiar

        synchronized (frameImages) {
            frameImages.put(frameNumber, image);
        }
        synchronized (frameStatuses){
            frameStatuses.put(frameNumber, Boolean.FALSE);
        }

        synchronized (pairsList) {
            pairsList.add(checkFirstPair(frameStatuses));
            pairsList.notify();
        }

    }

     private int checkFirstPair(TreeMap<Integer, Boolean> framesArray){

        int lowestFrame = framesArray.firstKey();
        int secondLowestFrame = framesArray.higherKey(lowestFrame);
        while(secondLowestFrame - lowestFrame != 1 || framesArray.get(lowestFrame)){
            lowestFrame = secondLowestFrame;
            if(framesArray.lastKey() == lowestFrame) {
                framesArray.put(lowestFrame, Boolean.TRUE);
                return lowestFrame;
            }
            secondLowestFrame = framesArray.higherKey(lowestFrame);
        }
        framesArray.put(lowestFrame, Boolean.TRUE);
        return lowestFrame;

    }

//    for(int i=0; i<threadsNo; i++) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                synchronized (pairsList) {
//                    try {
//                        pairsList.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                while (!pairsList.isEmpty()) {
//
//                    int frameNo;
//                    synchronized (pairsList){
//                        frameNo = pairsList.get(0);
//                        if(frameImages.get(frameNo + 1) == null){
//                            break;
//                        }
//                        pairsList.remove(0);
//                    }
//                    Point2D.Double result = imgConvrt.convert(frameNo, frameImages.get(frameNo), frameImages.get(frameNo + 1));
//
//                    results.put(frameNo, result);
//                    sendResults();
//                }
//
//            }
//        }).start();
//
//
//    }
}

