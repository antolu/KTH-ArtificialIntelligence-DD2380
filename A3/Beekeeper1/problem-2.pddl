;; Problem definition
(define (problem problem-1)

  ;; Specifying the domain for the problem
  (:domain honey-domain)

  ;; Objects definition
  (:objects
    ; Positions
    home
    forest-1 forest-2 forest-3 forest-4 forest-5
    garden
    ; Arms
    left right
    ; Bee-houses
    bee-house-1 bee-house-2 bee-house-3 bee-house-4
    ; Honey pots
    honey-pot-1 honey-pot-2 honey-pot-3
    ; Costumes
    costume
  )

  ;; Intial state of problem 1
  (:init
    ;; Declaration of the objects
    ; We initialize the positions
    (POSITION home)
    (POSITION forest-1) (POSITION forest-2) (POSITION forest-3)
      (POSITION forest-4) (POSITION forest-5)
    (POSITION garden)
    ; Arms
    (ARM left) (ARM right)
    ; Objects
    ; Bee houses
    (OBJECT bee-house-1) (OBJECT bee-house-2)
      (OBJECT bee-house-3) (OBJECT bee-house-4)
    (BEE-HOUSE bee-house-1) (BEE-HOUSE bee-house-2)
      (BEE-HOUSE bee-house-3) (BEE-HOUSE bee-house-4)
    ; Honey pots
    (OBJECT honey-pot-1) (OBJECT honey-pot-2) (OBJECT honey-pot-3)
    (HONEY-POT honey-pot-1) (HONEY-POT honey-pot-2) (HONEY-POT honey-pot-3)
    ; Costumes
    (OBJECT costume)
    (COSTUME costume)

    ;; Declaration of the predicates of the objects
    ; We set both arms free
    (free left) (free right)
    ; We set all honey pots empty
    (empty honey-pot-1) (empty honey-pot-2) (empty honey-pot-3)
    ; We set all the bee-houses as having honey
    (has-honey bee-house-1) (has-honey bee-house-2) (has-honey bee-house-3)
      (has-honey bee-house-4)
    ; We set the person at home
    (person-at home)
    ; We set the bee houses in the forests
    (object-at bee-house-1 forest-1) (object-at bee-house-2 forest-1)
      (object-at bee-house-3 forest-2) (object-at bee-house-4 forest-4)
    ; We set the costume in the garden
    (object-at costume garden)
    ; We set the honey pots at home
    (object-at honey-pot-1 home) (object-at honey-pot-2 home)
      (object-at honey-pot-3 home)
  )

  ;; Goal specification
  (:goal
    (and
      ; We want the honey pots at home
      (object-at honey-pot-1 home)
      (object-at honey-pot-2 home)
      (object-at honey-pot-3 home)
      ; We want the costume stored in its spot
      (object-at costume garden)
      ; We want the honey pots full of honey
      (not (empty honey-pot-1))
      (not (empty honey-pot-2))
      (not (empty honey-pot-3))
    )
  )

)
