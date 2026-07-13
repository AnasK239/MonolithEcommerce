import {configureStore} from "@reduxjs/toolkit"
import { ProductReducer } from "./ProductReducer";
import { errorReducer } from "./errorReducer";


const store = configureStore({
    reducer: {
        products: ProductReducer,
        errors: errorReducer,
    },
    preloadedState: {},
});


export default store