package com.cjw.rxjavademo;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.test_bt) {
            OperatorCombination operator = new OperatorCombination();
            operator.switchOnNext();
        }
    }

    /**
     * 组合操作符
     */
    class OperatorCombination {

        public void zip() {
            // zip
            // 使用一个指定的函数将多个Observable发射的数据组合在一起
            // 然后将这个函数的结果作为单项数据发射,严格周期顺序进行合并,不能单独发射
            // 注意:
            // 只有每个observable都生成一个数据时,才会统一发射一次
            // 当不满足所有observable都有数据可产品时,不再发射数据

            Observable<String> observable1 = Observable.just("a1", "a2", "a3");
            Observable<String> observable2 = Observable.just("b1", "b2", "b3", "a4");
            Observable.zip(observable1, observable2, new Func2<String, String, String>() {
                @Override
                public String call(String s, String s2) {
                    return s + " , " + s2;
                }
            }).subscribe(new Action1<String>() {
                @Override
                public void call(String s) {
                    Log.d(TAG, "call: " + s);
                }
            });

            // call: a1 , b1
            // call: a2 , b2
            // call: a3 , b3
        }

        public void switchOnNext() {
            // switchOnNext
            // 把一组Observable转换成一个Observable,如果在同一个时间内产生两个或多个Observable产生的数据,只发射最后一个Observable产生的数据

            Log.d(TAG, "startTime: " + System.currentTimeMillis());
            Observable<Observable<String>> observable = Observable.interval(2000, 500, TimeUnit.MILLISECONDS).map(new Func1<Long, Observable<String>>() {
                @Override
                public Observable<String> call(final Long aLongOutside) {
                    // 每隔300ms产生一组数据（0,1,2,3,4)
                    return Observable.interval(0, 300, TimeUnit.MILLISECONDS).map(new Func1<Long, String>() {
                        @Override
                        public String call(Long aLongInside) {
                            return "aLongOutside = " + aLongOutside + " , aLongInside = " + aLongInside + " , time : " + System.currentTimeMillis();
                        }
                    }).take(5);
                }
            }).take(2);

            Observable.switchOnNext(observable).subscribe(new Action1<String>() {
                @Override
                public void call(String s) {
                    Log.d(TAG, "call: " + s);
                }
            });

            // startTime: 1476949745501
            // call: aLongOutside = 0 , aLongInside = 0 , time : 1476949747516
            // call: aLongOutside = 0 , aLongInside = 1 , time : 1476949747765
            // call: aLongOutside = 1 , aLongInside = 0 , time : 1476949748015
            // call: aLongOutside = 1 , aLongInside = 1 , time : 1476949748263
            // call: aLongOutside = 1 , aLongInside = 2 , time : 1476949748513
            // call: aLongOutside = 1 , aLongInside = 3 , time : 1476949748764
        }

        public void startWith() {
            // startWith
            // 在源Observable发射数据之前,先发射一个指定的数据序列或数据项

            Observable.just(4, 5, 6).startWith(1, 2, 3).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    Log.d(TAG, "call: " + integer);
                }
            });

            // call: 1
            // call: 2
            // call: 3
            // call: 4
            // call: 5
            // call: 6
        }

        public void merge() {
            // merge
            // 将两个Observable发射的数据按照时间顺序进行组合,合并成一个Observable进行发射

            Log.d(TAG, "startTime: " + System.currentTimeMillis());
            Observable<String> observable1 = Observable.interval(0, 500, TimeUnit.MILLISECONDS)
                    .take(3).flatMap(new Func1<Long, Observable<String>>() {
                        @Override
                        public Observable<String> call(Long aLong) {
                            return Observable.just("o1: " + aLong + " , time : " + System.currentTimeMillis());
                        }
                    });

            Observable<String> observable2 = Observable.interval(500, 500, TimeUnit.MILLISECONDS)
                    .take(3).flatMap(new Func1<Long, Observable<String>>() {
                        @Override
                        public Observable<String> call(Long aLong) {
                            return Observable.just("o1: " + aLong + " , time : " + System.currentTimeMillis());
                        }
                    });

            Observable.merge(observable1, observable2)
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            Log.d(TAG, "call: " + s);
                        }
                    });

            // startTime: 1476946676174
            // call: o1: 0 , time : 1476946676177
            // call: o1: 0 , time : 1476946676680
            // call: o1: 1 , time : 1476946676680
            // call: o1: 1 , time : 1476946677175
            // call: o1: 2 , time : 1476946677176
            // call: o1: 2 , time : 1476946677675
        }

        public void join() {
            // join
            // 无论何时,如果一个Observable发射了一个数据项,只要在另一个Observable发射的数据项定义的时间窗口内,就将两个Observable发射的数据合并发射
            // 注意:
            // 只有当两个observable都发射过第一项数据时,并且符合上述条件,才能进行发射

            Observable<Long> observable1 = Observable.interval(2000, 1000, TimeUnit.MILLISECONDS).take(2);
            Observable<Long> observable2 = Observable.interval(2500, 1000, TimeUnit.MILLISECONDS).take(2);

            Log.d(TAG, "startTime: " + System.currentTimeMillis());
            observable1.join(observable2,
                    new Func1<Long, Observable<Long>>() {
                        @Override
                        public Observable<Long> call(Long aLong) {
                            Log.d(TAG, "o1: " + aLong + " , time : " + System.currentTimeMillis());
                            return Observable.just(aLong).delay(100, TimeUnit.MILLISECONDS);
                        }
                    },
                    new Func1<Long, Observable<Long>>() {
                        @Override
                        public Observable<Long> call(Long aLong) {
                            Log.d(TAG, "o2: " + aLong + " , time : " + System.currentTimeMillis());
                            return Observable.just(aLong).delay(100, TimeUnit.MILLISECONDS);
                        }
                    },
                    new Func2<Long, Long, Long>() {
                        @Override
                        public Long call(Long aLong1, Long aLong2) {
                            Log.d(TAG, "aLong1 = [" + aLong1 + "], aLong2 = [" + aLong2 + "]" + " , time : " + System.currentTimeMillis());
                            return aLong1 + aLong2;
                        }
                    }
            ).subscribe(new Subscriber<Long>() {
                @Override
                public void onCompleted() {
                    Log.d(TAG, "onCompleted");
                }

                @Override
                public void onError(Throwable e) {
                    // do nothing
                }

                @Override
                public void onNext(Long aLong) {
                    Log.d(TAG, "onNext: " + aLong + " , time : " + System.currentTimeMillis());
                }
            });

            // startTime: 1476945902410
            // o1: 0 , time : 1476945904414
            // o2: 0 , time : 1476945904914
            // aLong1 = [0], aLong2 = [0] , time : 1476945904914
            // onNext: 0 , time : 1476945904914
            // o1: 1 , time : 1476945905412
            // aLong1 = [1], aLong2 = [0] , time : 1476945905412
            // onNext: 1 , time : 1476945905412
            // o2: 1 , time : 1476945905914
            // aLong1 = [1], aLong2 = [1] , time : 1476945905914
            // onNext: 2 , time : 1476945905914
            // onCompleted
        }

        public void combineLatest() {
            // combineLatest
            // 当两个Observables中的任何一个发射了一个数据时,将两个Observables数据通过指定的规则进行处理,将结果进行发射
            // 注意:
            // 只有当两个observable都发射过第一项数据时,才能进行组合发射
            // 当每一个observable都发射首个数据后,在此发射任何数据都会找相应的另外一个observable的最新数据进行组合发射

            Observable<Long> observable1 = Observable.interval(0, 300, TimeUnit.MILLISECONDS).take(4);
            Observable<Long> observable2 = Observable.interval(500, 250, TimeUnit.MILLISECONDS).take(3);

            Log.d(TAG, "startTime: " + System.currentTimeMillis());
            Observable.combineLatest(observable1, observable2, new Func2<Long, Long, Long>() {
                @Override
                public Long call(Long aLong1, Long aLong2) {
                    Log.d(TAG, "aLong1 = [" + aLong1 + "], aLong2 = [" + aLong2 + "], time = [" + System.currentTimeMillis() + "]");
                    return aLong1 + aLong2;
                }
            }).subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    Log.d(TAG, "call: " + aLong);
                }
            });

            // startTime: 1476843262324
            // aLong1 = [1], aLong2 = [0], time = [1476843262824]
            // call: 1
            // aLong1 = [2], aLong2 = [0], time = [1476843262925]
            // call: 2
            // aLong1 = [2], aLong2 = [1], time = [1476843263075]
            // call: 3
            // aLong1 = [3], aLong2 = [1], time = [1476843263224]
            // call: 4
            // aLong1 = [3], aLong2 = [2], time = [1476843263324]
            // call: 5

            // 分析:
            // observable1 打印时间分别为:  0ms    300ms  600ms  900ms
            // observable2 打印时间分别为:  500ms  750ms  900ms  1000ms
            // 只有当两个observable都发射过第一项数据时,才能进行组合发射
            // 第1次发射时间: 500ms   a1 = 1 , a2 = 0
            // 第2次发射时间: 600ms   a1 = 2 , a2 = 0
            // 第3次发射时间: 750ms   a1 = 2 , a2 = 1
            // 第4次发射时间: 900ms   a1 = 3 , a2 = 1
            // 第5次发射时间: 1000ms  a1 = 3 , a2 = 2
        }

    }

    /**
     * 过滤操作符
     */
    class OperatorFilter {

        public void takeLast() {
            // takeLast
            // 与skipLast用法相反,只保留后面的n项数据进行发射,而忽略前面的结果

            Observable.just(1, 2, 3, 4, 5)
                    .takeLast(2)
                    .subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            Log.d(TAG, "call: " + integer);
                        }
                    });

            // call: 4
            // call: 5
        }

        public void take() {
            // take
            // 与skip用法相反,保留前面的n项数据进行发射,而忽略后面的结果

            Observable.just(1, 2, 3, 4, 5)
                    .take(2)
                    .subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            Log.d(TAG, "call: " + integer);
                        }
                    });

            // call: 1
            // call: 2
        }

        public void skipLast() {
            // skipLast
            // 跳过后面的n项数据不进行处理

            Observable.just(1, 2, 3, 4, 5)
                    .skipLast(2)
                    .subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            Log.d(TAG, "call: " + integer);
                        }
                    });

            // call: 1
            // call: 2
            // call: 3
        }

        public void skip() {
            // skip
            // 跳过前面的n项数据不进行处理

            Observable.just(1, 2, 3, 4, 5)
                    .skip(2)
                    .subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            Log.d(TAG, "call: " + integer);
                        }
                    });

            // call: 3
            // call: 4
            // call: 5
        }

        public void sample() {
            // sample
            // 取样,定期扫描源Observable产生的数据,发射最新的数据

            Observable.interval(100, TimeUnit.MILLISECONDS)
                    .sample(200, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            Log.d(TAG, "call: " + aLong);
                        }
                    });

            // 输出的数字均为偶数,结果会一直打印
            // call: 2
            // call: 4
            // call: 6
            // call: 8
        }

        public void ignoreElements() {
            // ignoreElements
            // 忽略所有数据,只保留终止通知(onError或onCompleted)

            Observable.just(100, 101, 102, 103, 104)
                    .ignoreElements()
                    .subscribe(new Subscriber<Integer>() {
                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "onError");
                        }

                        @Override
                        public void onNext(Integer integer) {
                            Log.d(TAG, "onNext: " + integer);
                        }
                    });

            // onCompleted
        }

        public void last() {
            // last
            // 末项,只发射末项或满足条件的末项数据

            Observable.just(100, 101, 102, 103, 104)
                    .last(new Func1<Integer, Boolean>() {
                        @Override
                        public Boolean call(Integer integer) {
                            return integer % 2 == 1;
                        }
                    }).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    Log.d(TAG, "call: " + integer);
                }
            });

            // call: 103
        }

        public void first() {
            // first
            // 首项,只发射首项或满足条件的首项数据

            Observable.just(100, 101, 102, 103, 104)
                    .first(new Func1<Integer, Boolean>() {
                        @Override
                        public Boolean call(Integer integer) {
                            return integer % 2 == 1;
                        }
                    }).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    Log.d(TAG, "call: " + integer);
                }
            });

            // call: 101
        }

        public void filter() {
            // filter
            // 对发射的数据进行过滤,只发射符合条件的数据

            Observable.just(101, 102, 103, 104)
                    .filter(new Func1<Integer, Boolean>() {
                        @Override
                        public Boolean call(Integer integer) {
                            return integer >= 102;
                        }
                    }).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    Log.d(TAG, "call: " + integer);
                }
            });

            // call: 102
            // call: 103
            // call: 104
        }

        public void elementAt() {
            // elementAt
            // 取值,取特定位置的数据项,索引是从0开始的

            Observable.just(100, 101, 102, 103, 104)
                    .elementAt(4)
                    .subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            Log.d(TAG, "call: " + integer);
                        }
                    });

            // call: 104
        }


        public void distinct() {
            // distinct
            // 去重,过滤掉重复数据项

            Observable.just(1, 1, 2, 2, 3)
                    .distinct()
                    .subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            Log.d(TAG, "call: " + integer);
                        }
                    });

            // call: 1
            // call: 2
            // call: 3
        }

        public void debounce() {
            // debounce
            // 在源Observable产生一个结果时开始计时,如果在规定的间隔时间内没有别的结果产生或者在此期间调用了onCompleted,则发射数据,否则忽略发射

            Observable.create(new Observable.OnSubscribe<Integer>() {

                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    try {
                        // 产生结果的间隔时间分别为100、200、300...900毫秒
                        for (int i = 1; i < 10; i++) {
                            subscriber.onNext(i);
                            SystemClock.sleep(i * 100);
                        }
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }
            }).debounce(400, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            Log.d(TAG, "call: " + integer);
                        }
                    });

            // call: 5
            // call: 6
            // call: 7
            // call: 8
            // call: 9
        }
    }

    /**
     * 转换类操作符
     */
    class OperatorChange {

        public void window() {
            // window
            // 它可以批量或者按周期性从Observable收集数据到一个集合,然后把这些数据集合打包发射
            // 而不是一次发射一个数据,类似于Buffer,但Buffer发射的是数据,Window发射的是Observable

            Observable.range(1, 5).window(2)
                    .subscribe(new Action1<Observable<Integer>>() {
                        @Override
                        public void call(Observable<Integer> integerObservable) {
                            Log.d(TAG, "call() called with: integerObservable = [" + integerObservable + "]");
                            integerObservable.subscribe(new Action1<Integer>() {
                                @Override
                                public void call(Integer integer) {
                                    Log.d(TAG, "call() called with: integer = [" + integer + "]");
                                }
                            });
                        }
                    });

            // call() called with: integerObservable = [rx.subjects.UnicastSubject@220a7e3f]
            // call() called with: integer = [1]
            // call() called with: integer = [2]
            // call() called with: integerObservable = [rx.subjects.UnicastSubject@14a9230c]
            // call() called with: integer = [3]
            // call() called with: integer = [4]
            // call() called with: integerObservable = [rx.subjects.UnicastSubject@3bf9c455]
            // call() called with: integer = [5]
        }

        public void scan() {
            // scan
            // 遍历源Observable产生的结果,通过自定义转换规则,依次输出结果给订阅者

            Observable.range(0, 4).scan(new Func2<Integer, Integer, Integer>() {
                @Override
                public Integer call(Integer lastRes, Integer curRes) {
                    // 第一个参数是上次的结算结果
                    // 第二个参数是当此的源observable的输入值
                    return lastRes + curRes;
                }
            }).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    Log.d(TAG, "call: " + integer);
                }
            });

            // call: 0          lastRes:0    curRes:0
            // call: 1          lastRes:0    curRes:1
            // call: 3          lastRes:1    curRes:2
            // call: 6          lastRes:3    curRes:3
        }

        public void groupBy() {
            // groupBy
            // 分组

            List<String> dataList = new ArrayList<>();
            Collections.addAll(dataList, "10", "str1", "str2", "20", "30");

            Observable.from(dataList)
                    .groupBy(new Func1<String, Boolean>() {
                        @Override
                        public Boolean call(String s) {
                            return s.matches("^(\\d)+$");
                        }
                    }).subscribe(new Action1<GroupedObservable<Boolean, String>>() {
                @Override
                public void call(final GroupedObservable<Boolean, String> booleanStringGroupedObservable) {
                    booleanStringGroupedObservable.subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            Log.d(TAG, "call: key : " + booleanStringGroupedObservable.getKey() + " , value : " + s);
                        }
                    });
                }
            });

            // call: key : true , value : 10
            // call: key : false , value : str1
            // call: key : false , value : str2
            // call: key : true , value : 20
            // call: key : true , value : 30
        }

        public void map() {
            // map
            // 映射,一般用于对原始的数据进行加工处理,返回一个加工过后的数据

            Observable.just(123)
                    .map(new Func1<Integer, String>() {
                        @Override
                        public String call(Integer integer) {
                            return String.valueOf(integer);
                        }
                    }).subscribe(new Action1<String>() {
                @Override
                public void call(String s) {
                    Log.d(TAG, "call: " + s);
                }
            });

            // call: 123
        }

        public void flatMap() {
            // flatMap
            // 扁平映射,作用是将一个原始Observable发射的数据进行变化,输出一个或多个Observable,
            // 然后将这些Observable发射的数据平坦化的放进一个单独的Observable（参数一般是Func1）

            Observable.just(123)
                    .flatMap(new Func1<Integer, Observable<String>>() {
                        @Override
                        public Observable<String> call(Integer integer) {
                            String str = String.valueOf(integer);
                            String[] arr = new String[str.length()];
                            for (int i = 0; i < str.length(); i++) {
                                arr[i] = String.valueOf(str.charAt(i));
                            }
                            return Observable.from(arr);
                        }
                    }).subscribe(new Action1<String>() {
                @Override
                public void call(String s) {
                    Log.d(TAG, "call: " + s);
                }
            });

            // call: 1
            // call: 2
            // call: 3
        }

        public void bufferDemo2() {
            // 周期收集
            Observable.create(new Observable.OnSubscribe<String>() {

                @Override
                public void call(Subscriber<? super String> subscriber) {
                    while (true) {
                        subscriber.onNext("消息:" + System.currentTimeMillis());
                        SystemClock.sleep(2000);
                    }
                }
            }).subscribeOn(Schedulers.io())
                    .buffer(3, TimeUnit.SECONDS)
                    .subscribe(new Subscriber<List<String>>() {
                        @Override
                        public void onCompleted() {
                            Log.d(TAG, "onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "onError");
                        }

                        @Override
                        public void onNext(List<String> strings) {
                            Log.d(TAG, "onNext: " + strings.toString());
                        }
                    });

            // onNext: [消息:1476434304084, 消息:1476434306085]
            // onNext: [消息:1476434308085]
            // onNext: [消息:1476434310085, 消息:1476434312085]
            // onNext: [消息:1476434314086]
        }

        public void bufferDemo1() {

            // bufferDemo1
            // 可以简单的理解为缓存,它可以批量或者按周期性从Observable收集数据到一个集合,然后把这些数据集合打包发射,而不是一次发射一个数据

            // 批量收集
            Observable.range(1, 5).buffer(2).subscribe(new Subscriber<List<Integer>>() {
                @Override
                public void onCompleted() {
                    Log.d(TAG, "onCompleted");
                }

                @Override
                public void onError(Throwable e) {
                    Log.d(TAG, "onError");
                }

                @Override
                public void onNext(List<Integer> integers) {
                    Log.d(TAG, "onNext: " + integers.toString());
                }
            });

            // onNext: [1, 2]
            // onNext: [3, 4]
            // onNext: [5]
            // onCompleted
        }

    }

    /**
     * 创建类操作符
     */
    class OperatorCreate {

        public void delay() {
            // delay
            // 功能与timer操作符一样,但是delay用于在事件中,可以延迟发送事件中的某一次发送

            Log.d(TAG, "start: " + System.currentTimeMillis());
            Observable.just(1).delay(2, TimeUnit.SECONDS).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    Log.d(TAG, "stop: " + System.currentTimeMillis());
                    Log.d(TAG, "call: " + integer);
                }
            });

            // start: 1476432983761
            // stop: 1476432985761
            // call: 1
        }

        public void repeat() {
            // repeat
            // 会将一个Observable对象重复发射,接收值是发射的次数,依然订阅在computation Scheduler

            Observable.just(1).repeat(3).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    Log.d(TAG, "call: " + integer);
                }
            });

            // call: 1
            // call: 1
            // call: 1
        }

        public void error() {
            // error
            // 返回一个Observable,当有Observer订阅它时直接调用Observer的onError方法终止

            Observable.error(new Throwable("错误")).subscribe(new Subscriber<Object>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Log.d(TAG, "onError: " + e.getMessage());
                }

                @Override
                public void onNext(Object o) {

                }
            });

            // onError: 错误
        }

        public void never() {
            // never
            // 创建一个Observable不发射任何数据,也不给订阅它的Observer发出任何通知

            Observable.never().subscribe(new Subscriber<Object>() {
                @Override
                public void onCompleted() {
                    Log.d(TAG, "onCompleted");
                }

                @Override
                public void onError(Throwable e) {
                    Log.d(TAG, "onError");
                }

                @Override
                public void onNext(Object o) {
                    Log.d(TAG, "onNext");
                }
            });

            // 没有输出任何东西
        }

        public void empty() {
            // empty
            // 创建一个Observable不发射任何数据,而是立即调用onCompleted方法终止

            Observable<Object> observable = Observable.empty();
            observable.subscribe(new Subscriber<Object>() {
                @Override
                public void onCompleted() {
                    Log.d(TAG, "onCompleted");
                }

                @Override
                public void onError(Throwable e) {
                    // do nothing
                }

                @Override
                public void onNext(Object o) {
                    // do nothing
                }
            });

            // onCompleted
        }

        public void timer() {
            // timer
            // 会在指定时间后发射,注意其也是运行在computation Scheduler

            Log.d(TAG, "start: " + System.currentTimeMillis());
            Observable.timer(2, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            Log.d(TAG, "stop: " + System.currentTimeMillis());
                            Log.d(TAG, "call: " + aLong);
                        }
                    });

            // 相隔2秒
            // start: 1476412128573
            // stop: 1476412130573
            // call: 0
        }

        public void interval() {
            // interval
            // 所创建的Observable对象会从0开始,每隔固定的时间发射一个数字
            // 需要注意的是这个对象是运行在computation Scheduler,所以要更新UI需要在主线程中进行订阅

            Observable.interval(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            Log.d(TAG, "call: " + aLong);
                        }
                    });

            // 这个将一直打印,aLong从0开始
            // call: 0
            // call: 1
            // call: 2
            // call: 3
        }

        public void range() {
            // range
            // 根据输入的初始值【initial】和数量【number】发射number次,大于等于initial的值

            Observable.range(100, 3).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    Log.d(TAG, "call: " + integer);
                }
            });

            // call: 100
            // call: 101
            // call: 102
        }

        public void defer() {
            // defer
            // 只有当有Subscriber来订阅的时候才会创建一个新的Observable对象,也就是说每次订阅都会得到一个刚创建的最新的Observable对象
            // 这可以确保Observable对象里的数据是最新的,而just则没有创建新的Observable对象

            Action1<String> act1 = new Action1<String>() {
                @Override
                public void call(String s) {
                    Log.d(TAG, s);
                }
            };

            Observable<String> defer = Observable.defer(new Func0<Observable<String>>() {
                @Override
                public Observable<String> call() {
                    Object obj = new Object();
                    return Observable.just("defer : hashcode = " + obj.hashCode());
                }
            });

            defer.subscribe(act1);
            defer.subscribe(act1);
            defer.subscribe(act1);

            Observable<String> just = Observable.just("just : hashcode = " + new Object().hashCode());
            just.subscribe(act1);
            just.subscribe(act1);
            just.subscribe(act1);

            // defer : hashcode = 1390750332
            // defer : hashcode = 1390753040
            // defer : hashcode = 1390753396
            // just : hashcode = 1390753808
            // just : hashcode = 1390753808
            // just : hashcode = 1390753808
        }

        public void from() {
            // from
            // 用来将某个对象转化为Observable对象,并且依次将其内容发射出去,from的接收值可以是集合或者数组,
            // 这个类似于just,但是just会将这个对象整个发射出去.比如说一个含有3个元素的集合,from会将集合分成3次发射,而使用just会发射一次来将整个的数组发射出去

            List<String> dataList = new ArrayList<>();
            Collections.addAll(dataList, "s1", "s2");
            Observable.from(dataList).subscribe(new Action1<String>() {
                @Override
                public void call(String s) {
                    Log.d(TAG, "call: " + s);
                }
            });

            // call: s1
            // call: s2
        }

        public void just() {
            List<String> dataList = new ArrayList<>();
            Collections.addAll(dataList, "s1", "s2");
            Observable.just(dataList).subscribe(new Action1<List<String>>() {
                @Override
                public void call(List<String> strings) {
                    Log.d(TAG, "call: " + strings.size());
                }
            });

            // call: 2
        }

        public void create() {
            Observable.create(new Observable.OnSubscribe<Integer>() {

                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    subscriber.onNext(1);
                    subscriber.onNext(2);
                    subscriber.onCompleted();
                    subscriber.onNext(3);
                }
            }).subscribe(new Subscriber<Integer>() {
                @Override
                public void onCompleted() {
                    Log.d(TAG, "onCompleted");
                }

                @Override
                public void onError(Throwable e) {
                    Log.d(TAG, "onError: " + e.getMessage());
                }

                @Override
                public void onNext(Integer integer) {
                    Log.d(TAG, "onNext: " + integer);
                }
            });

            // onNext: 1
            // onNext: 1
            // onCompleted
        }
    }
}
