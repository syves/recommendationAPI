# recommendationAPI

** Input Data
Given json contains 20K articles.
Each article contains set of attributes with one set value.

** Recommendation Engine Requirements
Calculate similarity of articles identified by sku based on their attributes values.
The number of matching attributes is the most important metric for defining similarity.
In case of a draw, attributes with name higher in alphabet (a is higher than z) is weighted with heavier weight.

Example 1:
{"sku-1": {"att-a": "a1", "att-b": "b1", "att-c": "c1"}} is more similar to
{"sku-2": {"att-a": "a2", "att-b": "b1", "att-c": "c1"}} than to
{"sku-3": {"att-a": "a1", "att-b": "b3", "att-c": "c3"}}
Example 2:
{"sku-1": {"att-a": "a1", "att-b": "b1"}} is more similar to
{"sku-2": {"att-a": "a1", "att-b": "b2"}} than to
{"sku-3": {"att-a": "a2", "att-b": "b1"}}

** Recommendation request example
sku-123  > ENGINE > 10 most similar skus based on criteria described above with their corresponding weights.

## "sku-123":{"att-a":"att-a-2","att-b":"att-b-13","att-c":"att-c-7","att-d":"att-d-1","att-e":"att-e-7","att-f":"att-f-10","att-g":"att-g-1","att-h":"att-h-8","att-i":"att-i-7","att-j":"att-j-1"},

** Language requirements
Use any language you feel comfortable with

** Code requirements
Clean structured reusable code

** Expected delivery format
tgz file containing solution with simple instructions how to run data import and how to execute recommendation request
