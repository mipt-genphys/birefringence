package ru.mipt.physics.birefringence.app

import ru.mipt.physics.birefringence.Data
import ru.mipt.physics.birefringence.Evaluator
import ru.mipt.physics.birefringence.Vector


/**
 * Created by darksnake on 18-May-16.
 */


Vector readVector(String path) {
    List<Double> vals = new ArrayList<>();
    Evaluator.class.getResource(path).eachLine {
        vals.add(it.replace(",",".").toDouble()*Math.PI/180)
    }
    return new Vector(vals)
}

Data data = new Data();

data.phi1 = readVector("/fi1.txt");
data.psie = readVector("/psie.txt");
data.psio = readVector("/psio.txt");
data.a = 38*Math.PI/180;
data.aErr = data.a/100;


println(new Evaluator().checkAdjustment(data))
println(new Evaluator().calculateno(data))
println(new Evaluator().calculatene(data))

