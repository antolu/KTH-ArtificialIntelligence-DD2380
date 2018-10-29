;; Problem definition
(define (problem problem-1)

    ;; Specifying the domain for the problem
    (:domain honey-domain)

    ;; Objects definition
    (:objects
        ; Positions
        home
        forest-1 forest-2
        garden
        ; Arms
        left right
        ; Bee-houses
        bee-house-1 bee-house-2
        ; Honey pots
        honey-pot-1 honey-pot-2
        ; Costumes
        costume
    )

    ;; Initial state of problem 1
    (:init
        ;; Declaration of the objects
        ; WRITE YOUR CODE FOR THE OBJECTS DECLARATION HERE
        (POSITION home)
        (POSITION forest-1)
        (POSITION forest-2)
        (POSITION garden)
        ; Arms
        (ARM left) (ARM right)
        ; Objects
        ; Bee houses
        (OBJECT bee-house-1) (OBJECT bee-house-2)
        (BEE-HOUSE bee-house-1) (BEE-HOUSE bee-house-2)
        ; Honey pots
        (OBJECT honey-pot-1) (OBJECT honey-pot-2)
        (HONEY-POT honey-pot-1) (HONEY-POT honey-pot-2)
        ; Costumes
        (OBJECT costume)
        (COSTUME costume)

        ;; Declaration of the predicates of the objects
        ; WRITE YOUR CODE FOR THE PREDICATES HERE
        ; We set both arms free
        (free left) (free right)
        ; We set all honey pots empty
        (empty honey-pot-1) (empty honey-pot-2)
        ; We set all the bee-houses as having honey
        (has-honey bee-house-1) (has-honey bee-house-2)
        ; We set the person at home
        (person-at home)
        ; We set the bee houses in the forests
        (object-at bee-house-1 forest-1) (object-at bee-house-2 forest-2)
        ; We set the costume in the garden
        (object-at costume garden)
        ; We set the honey pots at home
        (object-at honey-pot-1 home) (object-at honey-pot-2 home)

        (road forest-1 forest-2) (road forest-2 forest-1) (road home garden) (road garden home)
        (road home forest-1) (road forest-1 home) (road forest-2 home) (road home forest-2)

        ;; Declaration of the numeric fluents
        ; WRITE YOUR CODE FOR THE FLUENTS (if any) HERE
        ;Path between house and forest-1
        (= (distance home forest-1) 18)
        (= (distance forest-1 home) 18)
        ;Path between forest-1 and forest-2
        (= (distance forest-1 forest-2) 5)
        (= (distance forest-2 forest-1) 5)
        ;Path between forest-2 and home
        (= (distance forest-2 home) 22)
        (= (distance home forest-2) 22)
        ;Path between home and garden
        (= (distance home garden) 3)
        (= (distance garden home) 3)
        ;No paths
        (= (distance garden forest-1) 10000)
        (= (distance garden forest-2) 10000)
        (= (distance forest-1 garden) 10000)
        (= (distance forest-2 garden) 10000)
        (= total-cost 0)
    )

    ;; Goal specification
    (:goal
    ; WRITE YOUR CODE FOR GOAL SPECIFICATIONS HERE
        (and
            ; We want the honey pots at home
            (object-at honey-pot-1 home)
            (object-at honey-pot-2 home)
            ; We want the costume stored in its spot
            (object-at costume garden)
            ; We want the honey pots full of honey
            (not (empty honey-pot-1))
            (not (empty honey-pot-2))
        )
    )

    ;; Metric to define how good is a plan
    ; WRITE YOUR CODE FOR THE METRIC HERE
    (:metric minimize 
        (total-cost)
    )
)
