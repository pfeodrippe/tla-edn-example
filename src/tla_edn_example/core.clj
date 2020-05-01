(ns tla-edn-example.core
  (:require
   [tla-edn.core :as tla-edn]
   [tla-edn.spec :as spec])
  (:import
   (java.io File)))

;; see `resources/example.tla`, this is the position
;; of the variables in the spec
(def vars-keys
  [:c1 :c2 :account :sender :receiver :money :pc])

;; `defop` behaves like `defn`, but it generates a class which
;; can be used to override a operator (check its args to see the options)
(spec/defop TransferMoney {:module "example"}
  "`vars` is used to get the state of the world (:money, :sender, :receiver)"
  [self vars]                   ; the operators arguments (check `resources/example.tla`)
  (let [self (tla-edn/to-edn self) ; `self` is a string indicating which process is calling this operator
        vars (zipmap vars-keys (tla-edn/to-edn vars))
        sender (get-in vars [:sender self])
        receiver (get-in vars [:receiver self])
        money (get-in vars [:money self])]
    ;; here we transfer the money from `sender` to `receiver`
    ;; and then we convert it back to TLA values
    (-> (:account vars)
        (update sender - money)
        (update receiver + money)
        tla-edn/to-tla-value)))

;; entry point
(defn -main
  []
  ;; run the spec
  ;; first arg is the path to the TLA spec
  ;; second arg is the config
  (spec/run-spec (.getAbsolutePath (File. "resources/example.tla"))
                 "example.cfg")
  ;; exit the program with status 0
  (System/exit 0))
