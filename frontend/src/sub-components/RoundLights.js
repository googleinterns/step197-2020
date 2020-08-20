import React from "react";
import styled from "@emotion/styled";

class RoundLights extends React.Component {
  constructor(props) {
    super(props);
    this.statusColors = [];
    for (let i = 0; i < this.props.currentRound; i++) {
      this.statusColors.push("#1aa260");
    }
    const remaining = this.props.totalRounds - this.props.currentRound;
    for (let i = 0; i < remaining; i++) {
      this.statusColors.push("#bbb");
    }
  }

  render() {
    const Dot = styled.div`
      display:flex;
      height: 6rem;
      width: 6rem;
      border-radius: 50%;
      margin-right: 10%;
    `;

    return this.statusColors.map((color, i) => {
      return <Dot style={{backgroundColor:`${color}`}} key={i}></Dot>;
    });
  }
}

export default RoundLights;