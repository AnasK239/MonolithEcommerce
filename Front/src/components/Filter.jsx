import { Button, FormControl, IconButton, InputLabel, MenuItem, Select, Tooltip } from "@mui/material";
import { useEffect, useState } from "react";
import { FiArrowDown, FiArrowUp, FiRefreshCw, FiSearch } from "react-icons/fi";
import { useLocation , useNavigate, useSearchParams} from "react-router-dom"

const Filter = () => {
    const categories = [
        { categoryId:1 , categoryName: "Electronics" },
        { categoryId:2, categoryName: "Clothing" },
        { categoryId:3 , categoryName: "Furniture" },
        { categoryId:4 , categoryName: "Toys" },
    ];


    const[searchParams , setSearchParams] = useSearchParams();
    const pathname = useLocation().pathname;
    const params = new URLSearchParams(searchParams);
    const navigate = useNavigate();

    const [category , setCategory] = useState("all");
    const [sortOrder , setSortOrder] = useState("asc");
    const [searchTerm , setSearchTerm] = useState("")

    useEffect(() => {
        const currentCategory = searchParams.get("category") || "all";
        const currentSortOrder = searchParams.get("sortby") || "asc";
        const currentSearchTerm = searchParams.get("keyword") || "";

        setCategory(currentCategory);
        setSortOrder(currentSortOrder);
        setSearchTerm(currentSearchTerm);
    } , [searchParams])

    useEffect(() => {
        const handler = setTimeout(() => {
            if (searchTerm) {
                searchParams.set("keyword" , searchTerm);
            } else {
                searchParams.delete("keyword");
            }
            navigate(`${pathname}?${searchParams.toString()}`)
        } , 700);

        return () => {
            clearTimeout(handler);
        }
    } , [searchParams , searchTerm , navigate , pathname])


    const handleCategoryChange = (event) => {
        const selectedCategory = event.target.value;

        if (selectedCategory === "all") {
            params.delete("category");
        } else {
            params.set("category", selectedCategory);
        }

        setCategory(selectedCategory);

        navigate(`${pathname}?${params.toString()}`);
    };

    const toggleSortOrder = () => {
        setSortOrder((prevOrder) => {
            const newOrder = (prevOrder === "asc") ? "desc" : "asc";
            params.set("sortby" , newOrder);
            navigate(`${pathname}?${params}`)
            return newOrder
        })
    };

    const handleClearFilter = () => {
        navigate({pathname : window.location.pathname});
    };

    return (
        <div className="flex lg:flex-row flex-col-reverse lg:justifiy-between justify-center items-start gap-4">
            
            <div className="relative flex items-center 2xl:w-112.5 sm:w-105 w-full">
                <input 
                    type = "text"
                    placeholder="Search Products"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="border-gray-700 text-slate-800 rounded-md py-2 pl-10 pr-4 w-full  focus:ring-2 focus:ring-[#1976d2]"/>
                <FiSearch className="absolute left-3 text-slate-800 size-{20}"/>
            </div>

            <div className="flex sm:flex-row flex-col gap-4 items-center">
                <FormControl
                    className="text-slate-800 border-slate-700"
                    variant="outlined"
                    size="small">
                        <InputLabel id="category-select-label">Category</InputLabel>
                        <Select
                        labelId="category-select-label"
                        value={category}
                        onChange={handleCategoryChange}
                        label="Category"
                        className="min-w-[120px] text-slate-800 border-slate-700">
                            
                            <MenuItem value="all">All</MenuItem>
                            {categories.map((item) => (
                                <MenuItem key={item.categoryId} value={item.categoryName}>
                                    {item.categoryName}
                                </MenuItem>
                            ))}
                        </Select>
                </FormControl>
            </div>

            {/* SORT BUTTON & CLEAR FILTER */}
            <Tooltip title="Sort by price">
                <Button variant="contained"
                color="primary"
                className="flex items-center gap-2 h-10"
                onClick={toggleSortOrder}>
                    {sortOrder === "asc" ? (
                        <FiArrowUp size={20}/>
                    ) : (
                        <FiArrowDown size = {20}/>)
                    }
                    Sort By
                </Button>
            </Tooltip>

            <button className="flex items-center gap-2 bg-rose-900 text-white px-3 py-2 rounded-md transition duration-300 ease-in shadow-md focus:outline-none"
            onClick={handleClearFilter}>
                
                <FiRefreshCw />
                <span className="font-semibold">Clear Filter</span>
            </button>
        </div>
    )
}


export default Filter