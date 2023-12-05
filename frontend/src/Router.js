import Router from 'vue-router';
import StorageUtil from "./javascript_modules/storage_util";
import Vue from 'vue';
import Login from './views/Login';
import App from './App';
import UserProfile from "./views/UserProfile";
import SearchUsers from "./views/SearchUsers";
import NotFound from "./views/NotFound";
import Signup from "./views/Signup"
import BusinessSignup from "./views/BusinessSignup";
import BusinessProfile from "./components/business/BusinessProfile";
import HomePage from "./views/HomePage";
import Product from "./views/Product";
import Inventory from "./views/Inventory"
import BusinessManagement from "./views/BusinessManagement";
import CommunityMarketplace from "./views/CommunityMarketplace";
import MyListings from "./views/SaleListingManager";
import SaleListing from "./views/SaleListing";
import NotificationView from "./views/NotificationView"
import MyCardsView from "./views/MyCardsView";
import SaleItemViewDialog from "./components/sale-item-search/SaleItemViewDialog";

Vue.use(Router);

// Base url as defined in .env file
const BASE_URL = process.env.VUE_APP_BASE_URL;

/**
 * This defines the router and the routers properties that are used for directing to pages.
 * The last item in the routes MUST be the redirect to the 404 page as the regex "*" will catch
 * all so no paths beyond that point will be reachable.
 * @type {VueRouter}
 */
const router = new Router ({
    mode: 'history',
    base: BASE_URL,
    component: {App},
    // Routes are defined here
    routes: [
        {
            path: "/logout", // dummy URL, so that we can tell if a user has specifically requested to logout
            redirect: "/login"
        },
        {
            path: "/profile/:id",
            name: "Profile",
            component: UserProfile,
            meta: {
                requiresAuth: true
            }
        },
        {
            path: "/home",
            name: "HomePage",
            component: HomePage,
            children: [
                {
                    path:":id",
                    name: "FullSaleListingView",
                    component: SaleItemViewDialog,
                    props: true,
                    meta: {
                        showModal: true
                    }
                }
            ],
            meta: {
                requiresAuth: true
            }
        },
        {
            path: "/signup",
            name: "SignUp",
            component: Signup,
            meta: {
                guest: true
            }
        },
        {
            path: "/business/manage",
            name: "ManageBusiness",
            component: BusinessManagement,
        },
        {
            path: "/business-signup",
            name: "BusinessSignUp",
            component: BusinessSignup,
            meta: {
                requiresAuth: true,
                actingAsUser: true
            }
        },
        {
            path: "/business/:id",
            name: "BusinessProfile",
            component: BusinessProfile
        },
        {
            path: "/business/:businessId/add-product",
            name: "AddProduct",
            component: Product,
            meta: {
                requiresAuth: true,
                actingAsBusiness: true
            }
        },
        {
            path: "/business/:businessId/edit-product/:productId",
            name: "EditProduct",
            component: Product,
            meta: {
                requiresAuth: true,
                actingAsBusiness: true
            }
        },
        {
            path: "/business/:businessId/add-inventory",
            name: "AddInventory",
            component: Inventory,
            meta: {
                requiresAuth: true,
                actingAsBusiness: true
            }
        },
        {
            path: "/login",
            name: "Login",
            component: Login,
            meta: {
                guest: true
            }
        },
        {
            path: "/search",
            name: "SearchUsers",
            component: SearchUsers,
            meta: {
                requiresAuth: true
            }
        },
        {
            path: '/business/:id/listings',
            name: 'MyBusinessListings',
            component: MyListings,
            meta: {
                requiresAuth: true
            }
        },
        {
            path: "/community-marketplace",
            name: "CommunityMarketplace",
            component: CommunityMarketplace,
            meta: {
                requiresAuth: true
            }
        },
        {
            path: "/business/:businessId/add-sale-listing",
            name: "AddSaleListing",
            component: SaleListing,
            meta: {
                requiresAuth: true,
                actingAsBusiness: true
            }
        },
        {
            path: "/notifications/:id",
            name: "Notifications",
            component: NotificationView,
            meta: {
                requiresAuth: true,
                actingAsUser: true
            }
        },
        {
            path: "/my-cards/:id",
            name: "MyCards",
            component: MyCardsView,
            meta: {
                requiresAuth: true
            }
        },
        {
            path: "/user-cards/:id",
            name: "UserCards",
            component: MyCardsView,
            meta: {
                requiresAuth: true
            }
        },
        {
            path: "/404",
            name: "Not Found",
            component: NotFound,
        },
        {
            path: "/",
            redirect: "/login"
        },
        {
            path: "*",
            redirect: "/404"
        }
    ]
})


/** This method is called before each router navigation.
 * If the target route needs authentication and a token exists then navigation continues
 * and if a token does not exist they are taken to the login page.
 * If the target route is for guests and they don't have a toke they are taken to the target route, if they do have a
 * valid token their token is deleted and they are taken to the login page.
 * In all other cases (i.e. pages that don't require authentication and aren't for guests) are naviagated to straight
 * away.
 *
 * @param {Route} to    the target route being navigated to.
 * @param {Route} from    the current route being navigated from.
 * @param {Function} next    must be called to continue navigation. Action depends on the arguments given.
 */
router.beforeEach((to, from, next) => {
    // Dummy URL for logging out, so that a confirmation of logout pop-up doesn't appear.
    if (to.redirectedFrom === '/logout') {
        StorageUtil.setLoggedOut()
        next();
    }

    else if (to.matched.some(record => record.meta.requiresAuth)) {
        if (StorageUtil.isLoggedIn()) {

            if (to.matched.some(record => record.meta.actingAsBusiness)
                && StorageUtil.getActingAs() === "ACTING_AS_CURRENT_USER") {
                next({name: 'HomePage'});
            } else if (to.matched.some(record => record.meta.actingAsUser)
                && StorageUtil.getActingAs() !== "ACTING_AS_CURRENT_USER") {
                next({name: 'HomePage'});
            } else {
                next();
            }

        } else {
            StorageUtil.setLoggedOut();
            next({name: 'Login'});
        }
    } else {
        next();
    }
})

export default router;