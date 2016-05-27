package ru.mipt.physics.birefringence

import spock.lang.Specification

/**
 * Created by darksnake on 27-May-16.
 */
class EvaluatorTest extends Specification {
    def "test fitLine"() {
        given:
            Vector xVector = new Vector([1,2,3,4,5,6,7,8,9]);
            Vector yVecror = xVector + 1;
            Vector errVector = new Vector([0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1])
        when:
            def fitResult = new Evaluator().fitLine(xVector,yVecror,errVector);
        then:
            fitResult.base.round(3) == 1.0
            fitResult.slope.round(3) == 1.0;
    }
}
