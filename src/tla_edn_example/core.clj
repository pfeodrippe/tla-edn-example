(ns tla-edn-example.core
  (:require
   [tla-edn.core :as tla-edn]
   [tla-edn.spec :as spec])
  (:import
   (java.io File)))

;; see `resources/example.tla`
(def vars-keys
  [:c1 :c2 :account :sender :receiver :money :pc])

;; `defop` behaves like `defn`, but it generates a class which
;; can be used to override a operator (check its args to see the options)
(spec/defop TransferMoney {:module "example"}
  "`vars` is used to get the state of the world (:money, :sender, :receiver)"
  [self account vars]
  (let [self (tla-edn/to-edn self)
        vars (zipmap vars-keys (tla-edn/to-edn vars))
        sender (get-in vars [:sender self])
        receiver (get-in vars [:receiver self])
        money (get-in vars [:money self])]
    (-> (tla-edn/to-edn account)
        (update sender - money)
        (update receiver + money)
        tla-edn/to-tla-value)))

;; entry point
(defn -main
  []
  (spec/run-spec (.getAbsolutePath (File. "resources/example.tla"))
                 "example.cfg")
  (System/exit 0))
