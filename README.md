# TLA EDN Example

Code example using the library [TLA EDN](https://github.com/pfeodrippe/tla-edn).

## Usage

We will run the [TLA spec](resources/example.tla) which just transfers money
from one client to another and check some [invariants](resources/example.cfg).

When running for the first time, it will compile the operator and create the
`classes` folder, but it does not override the operator.

```shell
$ make run
# => ...
#    Compiling tla-edn override operators ... {tlc2.overrides.Operator_TransferMoney_402528661 #object[clojure.lang.Namespace 0x1ab6718 tla-edn-example.core]} ... ok
#    ...
#    TLA+ output (not using override operator yet)

```

Now running for the second time.

```shell
$ make run
# => ...
#    Loading TransferMoney operator override from tlc2.overrides.Operator_TransferMoney_402528661 with signature: <Java Method: public static java.lang.Object tlc2.overrides.Operator_TransferMoney_402528661.TransferMoney(java.lang.Object,java.lang.Object,java.lang.Object)>.
#    ...
#    TLA+ output
```

You can check that it fails if the code does not corresponds to the expected
behaviour, replace the operator `TransferMoney`
at [core.clj](src/tla_edn_example/core.clj) with the following code

```clojure
(spec/defop TransferMoney {:module "example"}
  "`vars` is used to get the state of the world (:money, :sender, :receiver)"
  [self account vars]
  (let [self (tla-edn/to-edn self)
        vars (zipmap vars-keys (tla-edn/to-edn vars))
        sender (get-in vars [:sender self])
        receiver (get-in vars [:receiver self])
        money (get-in vars [:money self])]
    (-> (tla-edn/to-edn account)
        (update sender + money)      ;; <-- Changed here to `+`
        (update receiver + money)
        tla-edn/to-tla-value)))
```

And run again

```shell
$ make run
# => ...
#    Error: Invariant ConstantBalance is violated.
#    ...
```
