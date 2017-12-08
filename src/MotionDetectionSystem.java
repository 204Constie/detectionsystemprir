import org.omg.CORBA.FREE_MEM;
//import sun.jvm.hotspot.jdi.BooleanTypeImpl;

import java.awt.geom.Point2D;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by constie on 01.12.2017.
 */
public class MotionDetectionSystem implements MotionDetectionSystemInterface {
    private ConcurrentSkipListMap<Integer, Boolean> frameStatuses = new ConcurrentSkipListMap<>();
    private ConcurrentSkipListMap<Integer, int[][]> frameImages = new ConcurrentSkipListMap<>();
    private BlockingQueue taskQueue = null;

//    private ConcurrentSkipListMap<Integer, ConcurrentSkipListMap<Boolean, int[][]>> frameMap = new ConcurrentSkipListMap<>();
//    private Map<Integer, Point2D.Double> results = Collections.synchronizedMap(new TreeMap<>());
    private ImageConverterInterface imgConvrt;
    private ResultConsumerInterface rcinter;
    private Point2D.Double imgResults;
    private int threadsNo = 0;
    private int iter = 0;
//    private AtomicInteger frameNo =  new AtomicInteger(0);
//    private AtomicInteger iter = new AtomicInteger(0);
    private LinkedBlockingQueue<Integer> pairsList = new LinkedBlockingQueue<>();
    private ConcurrentSkipListMap<Integer, Point2D.Double> results = new ConcurrentSkipListMap<>();
    private Map<Integer, Thread> threadsArray = Collections.synchronizedMap(new TreeMap<>());

    @Override
    public void setThreads(int threads) {
        threadsNo = threads;
//        threads to ilosc watkow ktora ma wykonywac prace
//        w trakcie pracy programu moze sie zmieniac:
//        -  zwiekszenie liczby watkow - jak najszybsze zagospodarownaie
//        -  zmienjszenie liczby watkow - stopniowe ich wygaszanie
//        worker();
    }

    @Override
    public void setImageConverter(ImageConverterInterface ici) {
        imgConvrt = ici;

//        jdenokrotne wykonanie przed pierwszym dostarczeniem obrazu
//        przekazanie referencji do obiektu odpowiedzialnego za przetwarzanie obrazu
        ExecutorService executor = Executors.newFixedThreadPool(threadsNo);

        for (int i = 0; i < 10; i++) {
            executor.execute(worker());
        }
        executor.shutdown();
//        worker();

    }
    public Runnable worker(){
//        for(int i=0; i<threadsNo; i++) {
//            threadsArray.put(i+1,
                    return new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            PMO_SystemOutRedirect.println("now running: " + Thread.currentThread().getName());
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
                                PMO_SystemOutRedirect.println("now running: " + Thread.currentThread().getName());
                                int frameNo = 0;
//                        synchronized (pairsList) {
                                try {
                                    frameNo = pairsList.take();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
//                            PMO_SystemOutRedirect.println("----------frameImages.size()-1-------------: " + (frameImages.size()-1) );
//                            PMO_SystemOutRedirect.println("+++++++++frameImages.size()-1+++++++++: " + (pairsList.size()-1) );
//                            pairsList.remove(0);
//                        }
                                if((frameNo != (frameImages.size()-1)) && frameImages.get(frameNo) != null && frameImages.get(frameNo + 1) != null) {
                                    Point2D.Double result = imgConvrt.convert(frameNo, frameImages.get(frameNo), frameImages.get(frameNo + 1));
//                            PMO_SystemOutRedirect.println("---++++++++++++++++----: " + frameNo + " " + result );
                                    synchronized (results) {
                                        results.put(frameNo, result);
                                    }
                                    sendResults();

                                }
                                sendResults();

                            }


                        }
                    });
//            threadsArray.get(i+1).start();
//
//
//        }
    }

    @Override
    public void setResultListener(ResultConsumerInterface rci) {
        rcinter = rci;

//        jdenokrotne wykonanie przed pierwszym dostarczeniem obrazu
//        przekazywanie wynikow do listenera

    }

      synchronized public void sendResults(){
//        PMO_SystemOutRedirect.println("+++++++++++++++: " + results.get(0) + " iter: " + iter);
//         for(int u=0; u<10; u++){
//             PMO_SystemOutRedirect.println("threadsArray.get(u+1).getState(): " + threadsArray.get(u+1).getName() + " " +threadsArray.get(u+1).getState());
//         }
        while(results.get(iter) != null){
//            PMO_SystemOutRedirect.println("results.get(iter): " + results.get(iter));
            rcinter.accept(iter, results.get(iter));
            results.remove(iter);
            iter++;
        }
        return;
    }

    @Override
    public void addImage(int frameNumber, int[][] image) {

//        przekazywanie obrazu do przetworzenia
//        wg frameNumber (od 0 wlacznie)
//        obrazy moga byc przekazywanie w dowolnej kolejnosci
//        wszystkie obrazy maja ten sam rozmiar
//        synchronized (frameMap){
//            frameMap.put(frameNumber, new ConcurrentSkipListMap());
//            frameMap.get(frameNumber).put(Boolean.FALSE, image);
//        }

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
    private int checkFirstPair(ConcurrentSkipListMap<Integer, Boolean> framesArray){

          int lowestFrame = framesArray.firstKey();
          int secondLowestFrame = framesArray.higherKey(lowestFrame);
          while (secondLowestFrame - lowestFrame != 1 || framesArray.get(lowestFrame)) {
              lowestFrame = secondLowestFrame;
              if (framesArray.lastKey() == lowestFrame) {
                  framesArray.put(lowestFrame, Boolean.TRUE);
                  PMO_SystemOutRedirect.println("low: " + lowestFrame);
                  return lowestFrame;
              }
              secondLowestFrame = framesArray.higherKey(lowestFrame);
          }
          framesArray.put(lowestFrame, Boolean.TRUE);
          PMO_SystemOutRedirect.println("lo: " + lowestFrame);
          return lowestFrame;


    }

//    private int checkFirstPair(ConcurrentSkipListMap<Integer, ConcurrentSkipListMap<Boolean, int[][]>> framesArray){
//
//        int lowestFrame = framesArray.firstKey();
//        int secondLowestFrame = framesArray.higherKey(lowestFrame);
//        while (secondLowestFrame - lowestFrame != 1 || framesArray.get(lowestFrame).firstKey()) {
//            lowestFrame = secondLowestFrame;
//            if (framesArray.lastKey() == lowestFrame) {
//                framesArray.put(lowestFrame, Boolean.TRUE);
//                PMO_SystemOutRedirect.println("low: " + lowestFrame);
//                return lowestFrame;
//            }
//            secondLowestFrame = framesArray.higherKey(lowestFrame);
//        }
//        framesArray.put(lowestFrame, Boolean.TRUE);
//        PMO_SystemOutRedirect.println("lo: " + lowestFrame);
//        return lowestFrame;
//
//
//    }



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

