## data creation
---------------

1. Go to `../linegraphproducer/codes/` 

2. run `configProducer.py`. This will produce config files at `../linegraphproducer/configs`

3. run `configPlotter.py $i` to produce svg files at `../linegraphproducer/data/`

## curve separation
-------------------

1. Go to `../linegraphproducer/analysis`.

2. Run `CurveExtractionArchitecture` from sbt. 

## evaluation
-------------------

1. Go to `../evaluation`.

2. Run `CreateEvaluationConfig.py ../linegraphproducer/data/$i/$i-sps` (make sure **not** to put trailing slash). This will create evaluation config files at `evalconfig` directory.
  
3. Run `evaluate.py evalconfigs/$i-sps.json $k` to create result files at `../results/run<$k>` directory.  
  
