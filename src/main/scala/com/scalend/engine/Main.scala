package com.scalend.engine

import net.liftweb.json._
import org.apache.spark.mllib.recommendation.{MatrixFactorizationModel, Rating, ALS}
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by shailesh.gupta on 19/10/15.
 */
object Main extends App{

    implicit val formats = net.liftweb.json.DefaultFormats

    def convertJsonToClass[A](content: String)(implicit mf: scala.reflect.Manifest[A]): A = {
        val json = parse(content)
        json.extract[A]
    }

    var conf = new SparkConf().setMaster("local").setAppName("recomend").set("spark.driver.host","127.0.0.1")
    val sc = new SparkContext( conf )

//    val data = sc.textFile("data/mllib/als/test.data")
//    val data = sc.textFile("user_views.json")
    val data = sc.textFile("sample_data.json")

    val ratings = data.flatMap( y => {
        val mapped = convertJsonToClass[input](y.mkString)
        val p = mapped.pid_list.map(k =>   Rating(mapped.uuid ,k , 1 ))
        p
    })

//    ratings.foreach( p => println(p))

    // Build the recommendation model using ALS
        val rank = 10
        val numIterations = 10
        val model = ALS.train(ratings , rank, numIterations, 0.01)

        // Evaluate the model on rating data
        val usersProducts = ratings.map { case Rating(user, product, rate) =>
            (user, product)
        }
        val predictions =
            model.predict(usersProducts).map { case Rating(user, product, rate) =>
                ((user, product), rate)
            }
        val ratesAndPreds = ratings.map { case Rating(user, product, rate) =>
            ((user, product), rate)
        }.join(predictions)
        val MSE = ratesAndPreds.map { case ((user, product), (r1, r2)) =>
            val err = (r1 - r2)
            err * err
        }.mean()
        println("Mean Squared Error = " + MSE)

        // Save and load model
        model.save(sc, "myModelPath")
        val sameModel = MatrixFactorizationModel.load(sc, "myModelPath")
}


class input(val uuid : Int , val pid_list : List[Int] , event_time_list : List[Double]) {

}
