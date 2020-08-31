import React, { useState, Component } from "react";
import Header from "../main-components/Header";
import Sidebar from "../main-components/Sidebar";
import styled from "@emotion/styled";
import EditCardContent from "../main-components/EditCardContent";

function EditCard(props) {
  // Handles mobile menu button and updates sidebar view
  const [sideSetting, setSideSetting] = useState("f");
  const handleClick = (e) => {
    console.log("Clicked");
    if (sideSetting === "f") {
      setSideSetting("t");
    } else {
      setSideSetting("f");
    }
  };

  return (
    <div className="App">
      <Header id="head" handleClick={handleClick}></Header>
      <div id="main">
        <Sidebar bool={sideSetting}></Sidebar>
        <EditCardContent
          cardKey={props.location.state.cardKey}
          textTranslated={props.location.state.textTranslated}
          rawText={props.location.state.rawText}
          imageBlobKey={props.location.state.imageBlobKey}
        ></EditCardContent>
      </div>
    </div>
  );
}

export default EditCard;